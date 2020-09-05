/*
	FUSE: Filesystem in Userspace
	Copyright (C) 2001-2007  Miklos Szeredi <miklos@szeredi.hu>

	This program can be distributed under the terms of the GNU GPL.
	See the file COPYING.
*/

#define	FUSE_USE_VERSION 26

#include <stdlib.h>
#include <fuse.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <limits.h>

//size of a disk block
#define	BLOCK_SIZE 512

//we'll use 8.3 filenames
#define	MAX_FILENAME 8
#define	MAX_EXTENSION 3

//How many files can there be in one directory?
#define MAX_FILES_IN_DIR (BLOCK_SIZE - sizeof(int)) / ((MAX_FILENAME + 1) + (MAX_EXTENSION + 1) + sizeof(size_t) + sizeof(long))
void * cs1550_init(struct fuse_conn_info* fi);

FILE *fp;
//The attribute packed means to not align these things
struct cs1550_directory_entry
{
	int nFiles;	//How many files are in this directory.
				//Needs to be less than MAX_FILES_IN_DIR

	struct cs1550_file_directory
	{
		char fname[MAX_FILENAME + 1];	//filename (plus space for nul)
		char fext[MAX_EXTENSION + 1];	//extension (plus space for nul)
		size_t fsize;					//file size
		long nIndexBlock;				//where the index block is on disk
	} __attribute__((packed)) files[MAX_FILES_IN_DIR];	//There is an array of these

	//This is some space to get this to be exactly the size of the disk block.
	//Don't use it for anything.
	char padding[BLOCK_SIZE - MAX_FILES_IN_DIR * sizeof(struct cs1550_file_directory) - sizeof(int)];
} ;

typedef struct cs1550_root_directory cs1550_root_directory;

#define MAX_DIRS_IN_ROOT (BLOCK_SIZE - sizeof(int)) / ((MAX_FILENAME + 1) + sizeof(long))

struct cs1550_root_directory
{
	int nDirectories;	//How many subdirectories are in the root
						//Needs to be less than MAX_DIRS_IN_ROOT
	struct cs1550_directory
	{
		char dname[MAX_FILENAME + 1];	//directory name (plus space for nul)
		long nStartBlock;				//where the directory block is on disk
	} __attribute__((packed)) directories[MAX_DIRS_IN_ROOT];	//There is an array of these

	//This is some space to get this to be exactly the size of the disk block.
	//Don't use it for anything.
	char padding[BLOCK_SIZE - MAX_DIRS_IN_ROOT * sizeof(struct cs1550_directory) - sizeof(int)];
} ;


typedef struct cs1550_directory_entry cs1550_directory_entry;

//How many entries can one index block hold?
#define	MAX_ENTRIES_IN_INDEX_BLOCK (BLOCK_SIZE/sizeof(long))

struct cs1550_index_block
{
      //All the space in the index block can be used for index entries.
			// Each index entry is a data block number.
      long entries[MAX_ENTRIES_IN_INDEX_BLOCK];
};

typedef struct cs1550_index_block cs1550_index_block;

//How much data can one block hold?
#define	MAX_DATA_IN_BLOCK (BLOCK_SIZE)

struct cs1550_disk_block
{
	//All of the space in the block can be used for actual data
	//storage.
	char data[MAX_DATA_IN_BLOCK];
};

typedef struct cs1550_disk_block cs1550_disk_block;

static long getFreeBitPos(FILE *fp)
{
	fseek(fp, -3 * BLOCK_SIZE, SEEK_END);
	int check = 0;
	long bitPos = 0;
	char bitMap[BLOCK_SIZE * 3];
	fread(&bitMap, 1, BLOCK_SIZE * 3, fp);
	//loop through bitmap to find hex value
	int i;
	for(i = 0; i < BLOCK_SIZE * 3; i++)
	{
		unsigned char byte = bitMap[bitPos];
		unsigned char result;
		int j;
		//loop through byte to see if there is a bit which is open
		for(j = 8; j > 0; j--)
		{
			result = (byte & (1<<(j-1)));
			if(result == 0)
			{
				byte = (byte | (1<<(j-1)));
				check = 1;
				break;
			}
			else
			{
				bitPos++;
			}
		}
		if(check == 1)
		{
			fseek(fp, -3 * BLOCK_SIZE + i, SEEK_END);
			fwrite(&byte, 1, 1, fp);
			break;
		}
	}
	return bitPos;
}

/*
 * Called whenever the system wants to know the file attributes, including
 * simply whether the file exists or not.
 *
 * man -s 2 stat will show the fields of a stat structure
 */
static int cs1550_getattr(const char *path, struct stat *stbuf)
{
	struct cs1550_root_directory *root = malloc(sizeof(struct cs1550_root_directory));
	fp = fopen(".disk", "rb+");

	fseek(fp, 0, SEEK_SET);
	fread(root, BLOCK_SIZE, 1, fp);

	int res = -ENOENT;

	char *directory;
	char *filename;
	char *extension;

	int pathLength = strlen(path);
	char pathDuplicate[pathLength];
	strcpy(pathDuplicate, path);

	directory = strtok(pathDuplicate, "/");
	filename = strtok(NULL, "/");
	filename = strtok(filename, ".");
	extension = strtok(NULL, ".");

	printf("PATH: %s\n", path);
	printf("DIRECTORY: %s\n", directory);
	printf("FILENAME: %s\n", filename);
	printf("EXTENSION: %s\n", extension);
	memset(stbuf, 0, sizeof(struct stat));

	//is path the root dir?
	if (strcmp(path, "/") == 0)
	{
		stbuf->st_mode = S_IFDIR | 0755;
		stbuf->st_nlink = 2;
		res = 0;
	}
	else
	{
		if(directory != NULL && filename == NULL && extension == NULL)
		{
			struct cs1550_directory_entry *fileDir;
			int i;
			for(i = 0; i < root->nDirectories; i++)
			{
				struct cs1550_directory testDir = root->directories[i];
				if(strcmp(testDir.dname, directory) == 0)
				{
					stbuf->st_mode = S_IFDIR | 0755;
					stbuf->st_nlink = 2;
					res = 0;

				  fileDir = malloc(sizeof(struct cs1550_directory_entry));
					fseek(fp, testDir.nStartBlock * BLOCK_SIZE, SEEK_SET);
					fread(fileDir, BLOCK_SIZE, 1, fp);
					break;
				}
			}
		}
		else if(filename != NULL && extension != NULL)
		{
			struct cs1550_directory_entry *fileDir = NULL;
			int i;
			for(i = 0; i < root->nDirectories; i++)
			{
				struct cs1550_directory testDir = root->directories[i];
				if(strcmp(testDir.dname, directory) == 0)
				{
					fileDir = malloc(sizeof(struct cs1550_directory_entry));
					fseek(fp, testDir.nStartBlock * BLOCK_SIZE, SEEK_SET);
					fread(fileDir, BLOCK_SIZE, 1, fp);
					break;
				}
			}

			int j;
			for(j = 0; j < fileDir->nFiles; j++)
			{
				if(strcmp(fileDir->files[j].fname, filename) == 0 && strcmp(fileDir->files[j].fext, extension) == 0)
				{
					stbuf->st_mode = S_IFREG | 0666;
					stbuf->st_nlink = 1;
					stbuf->st_size = fileDir->files[j].fsize;

					free(fileDir);
					res = 0;
					break;
				}
			}
		}
		else
		{
			fclose(fp);
			free(root);
			return -ENOENT;
		}
	}
	free(root);
	fclose(fp);
	return res;
}

/*
 * Called whenever the contents of a directory are desired. Could be from an 'ls'
 * or could even be when a user hits TAB to do autocompletion
 */
static int cs1550_readdir(const char *path, void *buf, fuse_fill_dir_t filler,
			 off_t offset, struct fuse_file_info *fi)
{
	//Since we're building with -Wall (all warnings reported) we need
	//to "use" every parameter, so let's just cast them to void to
	//satisfy the compiler
	(void) offset;
	(void) fi;

	struct cs1550_root_directory *root = malloc(sizeof(struct cs1550_root_directory));
	fp = fopen(".disk", "rb+");

	fseek(fp, 0, SEEK_SET);
	fread(root, BLOCK_SIZE, 1, fp);


	int pathLength = strlen(path);
	char pathDuplicate[pathLength];
	strcpy(pathDuplicate, path);

	char *directory = NULL;

	directory = strtok(pathDuplicate, "/");

	if(strcmp(path, "/") == 0)
	{
		int i;
		for(i = 0; i < root->nDirectories; i++)
		{
			struct cs1550_directory readDir = root->directories[i];
			filler(buf, readDir.dname, NULL, 0);
		}
	}
	else
	{
		struct cs1550_directory_entry *readSub = malloc(sizeof(struct cs1550_directory_entry));
		int i;
		for(i = 0; i < root->nDirectories; i++)
		{
			struct cs1550_directory readDir = root->directories[i];
			if(strcmp(readDir.dname, directory) == 0)
			{
					fseek(fp, readDir.nStartBlock * BLOCK_SIZE, SEEK_SET);
					fread(readSub, BLOCK_SIZE, 1, fp);
					break;
			}
		}
		for(i = 0; i < readSub->nFiles; i++)
		{
			struct cs1550_file_directory file = readSub->files[i];
			char concat[17];
			strcpy(concat, file.fname);
			strcat(concat, ".");
			strcat(concat, file.fext);
			filler(buf, concat, NULL, 0);
		}
	}

	//This line assumes we have no subdirectories, need to change
	//if (strcmp(path, "/") != 0)
	//return -ENOENT;

	//the filler function allows us to add entries to the listing
	//read the fuse.h file for a description (in the ../include dir)
	filler(buf, ".", NULL, 0);
	filler(buf, "..", NULL, 0);

	/*
	//add the user stuff (subdirs or files)
	//the +1 skips the leading '/' on the filenames
	filler(buf, newpath + 1, NULL, 0);
	*/
	fclose(fp);
	free(root);
	return 0;
}

/*
 * Creates a directory. We can ignore mode since we're not dealing with
 * permissions, as long as getattr returns appropriate ones for us.
 */
static int cs1550_mkdir(const char *path, mode_t mode)
{
	struct cs1550_root_directory *root = malloc(sizeof(struct cs1550_root_directory));
	fp = fopen(".disk", "rb+");

	fseek(fp, 0, SEEK_SET);
	fread(root, BLOCK_SIZE, 1, fp);

	(void) path;
	(void) mode;

	int length = strlen(path);
	char copyPath[length];
	strcpy(copyPath, path);
	char *directory, *test;
	directory = strtok(copyPath, "/");
	test = strtok(NULL, "/");

	if(strlen(directory) > MAX_FILENAME)
	{
		free(root);
		fclose(fp);
		return -ENAMETOOLONG;
	}
	if(test != NULL)
	{
		free(root);
		fclose(fp);
		return -EPERM;
	}
	if(root->nDirectories >= MAX_DIRS_IN_ROOT)
	{
		free(root);
		fclose(fp);
		return -ENOSPC;
	}
	int i;
	for(i = 0; i < root->nDirectories; i++)
	{
		struct cs1550_directory testDir = root->directories[i];
		if(strcmp(testDir.dname, directory) == 0)
		{
			free(root);
			fclose(fp);
			return -EEXIST;
		}
	}

	long bitPos = getFreeBitPos(fp);

	strcpy(root->directories[root->nDirectories].dname, directory);
	root->directories[root->nDirectories].nStartBlock = bitPos;
	//printf("Start Block: %d\n", root->directories[root->nDirectories].nStartBlock);
	//printf("Dir Name: %s\n", root->directories[root->nDirectories].dname);
	struct cs1550_directory_entry *newDir = malloc(sizeof(cs1550_directory_entry));
	newDir->nFiles = 0;

	fseek(fp, (bitPos * BLOCK_SIZE), SEEK_SET);
	fwrite(newDir, BLOCK_SIZE, 1, fp);

	root->nDirectories++;
	fseek(fp, 0, SEEK_SET);
	fwrite(root, BLOCK_SIZE, 1, fp);

	free(newDir);
	free(root);
	fclose(fp);

	return 0;
}

/*
 * Removes a directory.
 */
static int cs1550_rmdir(const char *path)
{
	(void) path;
    return 0;
}

/*
 * Does the actual creation of a file. Mode and dev can be ignored.
 *
 */
static int cs1550_mknod(const char *path, mode_t mode, dev_t dev)
{
	(void) mode;
	(void) dev;
	(void) path;

	struct cs1550_root_directory *root = malloc(sizeof(struct cs1550_root_directory));
	fp = fopen(".disk", "rb+");

	fseek(fp, 0, SEEK_SET);
	fread(root, BLOCK_SIZE, 1, fp);

	char *directory;
	char *filename;
	char *extension;

	int pathLength = strlen(path);
	char pathDuplicate[pathLength];
	strcpy(pathDuplicate, path);

	directory = strtok(pathDuplicate, "/");
	filename = strtok(NULL, "/");
	filename = strtok(filename, ".");
	extension = strtok(NULL, ".");

	if(strcmp(path, "/") == 0)
	{
		fclose(fp);
		free(root);
		return -EPERM;
	}
	if(strlen(filename) > MAX_FILENAME)
	{
		fclose(fp);
		free(root);
		return -ENAMETOOLONG;
	}
	if(strlen(extension) > MAX_EXTENSION)
	{
		fclose(fp);
		free(root);
		return -ENAMETOOLONG;
	}

	struct cs1550_directory_entry *readSub = malloc(sizeof(struct cs1550_directory_entry));
	int i;
	for(i = 0; i < root->nDirectories; i++)
	{
		struct cs1550_directory testDir = root->directories[i];
		if(strcmp(testDir.dname, directory) == 0)
		{
			fseek(fp, testDir.nStartBlock * BLOCK_SIZE, SEEK_SET);
			fread(readSub, BLOCK_SIZE, 1, fp);
			break;
		}
	}

	for(i = 0; i < readSub->nFiles; i++)
	{
		struct cs1550_file_directory testFile = readSub->files[i];
		if((strcmp(testFile.fname, filename) == 0) && (strcmp(testFile.fext, extension) == 0))
		{
			free(root);
			free(readSub);
			fclose(fp);
			return -EEXIST;
		}
	}
	free(readSub);

	long bitPos = getFreeBitPos(fp);
	long bitPosIndex = getFreeBitPos(fp);

	int subDirPos = -1;
	struct cs1550_directory_entry *updatedSubDir = malloc(sizeof(struct cs1550_directory_entry));
	for(i = 0; i < root->nDirectories; i++)
	{
		struct cs1550_directory index = root->directories[i];
		if(strcmp(index.dname, directory) == 0)
		{
			subDirPos = index.nStartBlock;
			fseek(fp, index.nStartBlock * BLOCK_SIZE, SEEK_SET);
			fread(updatedSubDir, BLOCK_SIZE, 1, fp);

			break;
		}
	}

	strcpy(updatedSubDir->files[updatedSubDir->nFiles].fname, filename);
	strcpy(updatedSubDir->files[updatedSubDir->nFiles].fext, extension);

	updatedSubDir->files[updatedSubDir->nFiles].nIndexBlock = bitPos;
	updatedSubDir->files[updatedSubDir->nFiles].fsize = 0;

	updatedSubDir->nFiles++;

	fseek(fp, (subDirPos * BLOCK_SIZE), SEEK_SET);
	fwrite(updatedSubDir, BLOCK_SIZE, 1, fp);

	struct cs1550_index_block *newFile = malloc(sizeof(cs1550_index_block));
	newFile->entries[0] = bitPosIndex;

	fseek(fp, (bitPos * BLOCK_SIZE), SEEK_SET);
	fwrite(newFile, BLOCK_SIZE, 1, fp);

	struct cs1550_disk_block *diskBlock = malloc(sizeof(cs1550_disk_block));

	fseek(fp, (bitPosIndex * BLOCK_SIZE), SEEK_SET);
	fwrite(diskBlock, BLOCK_SIZE, 1, fp);

	free(diskBlock);
	free(newFile);
	free(updatedSubDir);
	free(root);
	fclose(fp);

	return 0;
}

/*
 * Deletes a file
 */
static int cs1550_unlink(const char *path)
{
    (void) path;

    return 0;
}

/*
 * Read size bytes from file into buf starting from offset
 *
 */
static int cs1550_read(const char *path, char *buf, size_t size, off_t offset,
			  struct fuse_file_info *fi)
{
	(void) buf;
	(void) offset;
	(void) fi;
	(void) path;

	struct cs1550_root_directory *root = malloc(sizeof(struct cs1550_root_directory));
	fp = fopen(".disk", "rb+");

	fseek(fp, 0, SEEK_SET);
	fread(root, BLOCK_SIZE, 1, fp);

	char *directory;
	char *filename;
	char *extension;

	int pathLength = strlen(path);
	char pathDuplicate[pathLength];
	strcpy(pathDuplicate, path);

	directory = strtok(pathDuplicate, "/");
	filename = strtok(NULL, "/");
	filename = strtok(filename, ".");
	extension = strtok(NULL, ".");

	if(filename == NULL || extension == NULL)
	{
		free(root);
		fclose(fp);
		return -EISDIR;
	}

	struct cs1550_directory_entry *subDir = malloc(sizeof(struct cs1550_directory_entry));
	struct cs1550_index_block *file = malloc(sizeof(struct cs1550_index_block));
	struct cs1550_disk_block *diskBlock = malloc(sizeof(cs1550_disk_block));

	long isFileMade = 0;

	int i;
	for(i = 0; i < root->nDirectories; i++)
	{
		struct cs1550_directory testDir = root->directories[i];
		if(strcmp(testDir.dname, directory) == 0)
		{
			fseek(fp, testDir.nStartBlock * BLOCK_SIZE, SEEK_SET);
			fread(subDir, BLOCK_SIZE, 1, fp);
			break;
		}
	}
	for(i = 0; i < subDir->nFiles; i++)
	{
		struct cs1550_file_directory testFile = subDir->files[i];
		if(strcmp(testFile.fname, filename) == 0
		&& strcmp(testFile.fext, extension) == 0)
		{
			if(offset > testFile.fsize || testFile.fsize <= 0)
			{
				free(diskBlock);
				free(file);
				free(subDir);
				free(root);
				fclose(fp);
				return 0;
			}
			fseek(fp, testFile.nIndexBlock * BLOCK_SIZE, SEEK_SET);
			fread(file, BLOCK_SIZE, 1, fp);
			isFileMade = 1;
			break;
		}
	}
	if(isFileMade == 0)
	{
		free(diskBlock);
		free(file);
		free(subDir);
		free(root);
		fclose(fp);
		return -ENOENT;
	}
	int dataLeft = size;
	int dataBlockCounter = 0;
	int dataBlockPos = -1;
	int offsetInFile = offset%BLOCK_SIZE;

	while(file->entries[dataBlockCounter] != 0 && dataBlockCounter < MAX_ENTRIES_IN_INDEX_BLOCK)
	{
		dataBlockPos = file->entries[dataBlockCounter];
		fseek(fp, dataBlockPos * BLOCK_SIZE, SEEK_SET);
		fread(diskBlock, BLOCK_SIZE, 1, fp);

		for(i = 0; i < BLOCK_SIZE && i < dataLeft; i++)
		{
			buf[i] = diskBlock->data[i + offsetInFile];
			dataLeft--;
			if(dataLeft == 0)
				break;
		}
		offsetInFile = 0;
		dataBlockCounter++;
	}


	free(diskBlock);
	free(file);
	free(subDir);
	free(root);
	fclose(fp);

	//check to make sure path exists
	//check that size is > 0
	//check that offset is <= to the file size
	//read in data
	//set size and return, or error
	return size;
}

/*
 * Write size bytes from buf into file starting from offset
 *
 */
static int cs1550_write(const char *path, const char *buf, size_t size,
			  off_t offset, struct fuse_file_info *fi)
{
	(void) buf;
	(void) offset;
	(void) fi;
	(void) path;

	struct cs1550_root_directory *root = malloc(sizeof(struct cs1550_root_directory));
	fp = fopen(".disk", "rb+");

	fseek(fp, 0, SEEK_SET);
	fread(root, BLOCK_SIZE, 1, fp);

	char *directory;
	char *filename;
	char *extension;

	int pathLength = strlen(path);
	char pathDuplicate[pathLength];
	strcpy(pathDuplicate, path);

	directory = strtok(pathDuplicate, "/");
	filename = strtok(NULL, "/");
	filename = strtok(filename, ".");
	extension = strtok(NULL, ".");

	if(strcmp(path, "/") == 0)
	{
		fclose(fp);
		free(root);
		return -EPERM;
	}
	struct cs1550_directory_entry *subDir = malloc(sizeof(struct cs1550_directory_entry));
	struct cs1550_index_block *file = malloc(sizeof(struct cs1550_index_block));
	struct cs1550_disk_block *diskBlock = malloc(sizeof(cs1550_disk_block));
	long subDirPos = -1;
	long isFileMade = 0;
	long filePos = -1;
	long fileStartIndex = -1;
	int i;
	for(i = 0; i < root->nDirectories; i++)
	{
		struct cs1550_directory testDir = root->directories[i];
		if(strcmp(testDir.dname, directory) == 0)
		{
			subDirPos = testDir.nStartBlock;
			fseek(fp, testDir.nStartBlock * BLOCK_SIZE, SEEK_SET);
			fread(subDir, BLOCK_SIZE, 1, fp);
			break;
		}
	}
	for(i = 0; i < subDir->nFiles; i++)
	{
		struct cs1550_file_directory testFile = subDir->files[i];
		if(strcmp(testFile.fname, filename) == 0
		&& strcmp(testFile.fext, extension) == 0)
		{
			fseek(fp, testFile.nIndexBlock * BLOCK_SIZE, SEEK_SET);
			fread(file, BLOCK_SIZE, 1, fp);
			isFileMade = 1;
			fileStartIndex = testFile.nIndexBlock;
			filePos = i;
			break;
		}
	}
	if(isFileMade == 0)
	{
		long bitPos = getFreeBitPos(fp);
		fileStartIndex = bitPos;
		long bitPosIndex = getFreeBitPos(fp);

		strcpy(subDir->files[subDir->nFiles].fname, filename);
		strcpy(subDir->files[subDir->nFiles].fext, extension);
		subDir->files[subDir->nFiles].nIndexBlock = bitPos;
		subDir->files[subDir->nFiles].fsize = 0;
		subDir->nFiles++;

		fseek(fp, (subDirPos * BLOCK_SIZE), SEEK_SET);
		fwrite(subDir, BLOCK_SIZE, 1, fp);

		file->entries[0] = bitPosIndex;

		fseek(fp, (bitPos * BLOCK_SIZE), SEEK_SET);
		fwrite(file, BLOCK_SIZE, 1, fp);

		fseek(fp, (bitPosIndex * BLOCK_SIZE), SEEK_SET);
		fwrite(diskBlock, BLOCK_SIZE, 1, fp);
	}
	if(offset > subDir->files[filePos].fsize)
	{
		free(diskBlock);
		free(file);
		free(subDir);
		free(root);
		fclose(fp);
		return -EFBIG;
	}

	int dataLeft = size;
	int fileSize = subDir->files[filePos].fsize;
	long dataBlockPos =  file->entries[fileSize/BLOCK_SIZE];
	int offsetInFile = offset%BLOCK_SIZE;
	int prevBufPos = 0;

	fseek(fp, dataBlockPos * BLOCK_SIZE, SEEK_SET);
	fread(diskBlock, BLOCK_SIZE, 1, fp);

	if(offset > 0 && offset == fileSize)
	{
		for(i = 0; i < BLOCK_SIZE && prevBufPos <= dataLeft; i++)
		{
			diskBlock->data[offsetInFile + i] = buf[i];
			dataLeft--;
		}
		prevBufPos = i;
		if(dataLeft == 0)
		{
			subDir->files[filePos].fsize += size;
			fseek(fp, subDirPos * BLOCK_SIZE, SEEK_SET);
			fwrite(subDir, BLOCK_SIZE, 1, fp);
		}
		fseek(fp, dataBlockPos * BLOCK_SIZE, SEEK_SET);
		fwrite(diskBlock, BLOCK_SIZE, 1, fp);
	}
	free(diskBlock);
	int nextEntryBlock = 0;
	long nextBlock;
	long nextIndexPos;
	if(offset > 0 && offset == fileSize)
		nextEntryBlock++;
	while(dataLeft > 0)
	{
		nextIndexPos = fileSize/BLOCK_SIZE + nextEntryBlock;
		if(file->entries[nextIndexPos] == 0)
			nextBlock = getFreeBitPos(fp);
		else
			nextBlock = file->entries[nextIndexPos];

		diskBlock = malloc(sizeof(cs1550_disk_block));

		for(i = 0; i < BLOCK_SIZE && prevBufPos <= dataLeft; i++)
		{
			diskBlock->data[i] = buf[prevBufPos + i];
			dataLeft--;
			if(dataLeft == 0)
				break;
		}
		fseek(fp, nextBlock * BLOCK_SIZE, SEEK_SET);
		fwrite(diskBlock, BLOCK_SIZE, 1, fp);
		free(diskBlock);
		nextEntryBlock++;

		file->entries[nextIndexPos] = nextBlock;
	}

	subDir->files[filePos].fsize += size;
	fseek(fp, subDirPos * BLOCK_SIZE, SEEK_SET);
	fwrite(subDir, BLOCK_SIZE, 1, fp);

	fseek(fp, fileStartIndex * BLOCK_SIZE, SEEK_SET);
	fwrite(file, BLOCK_SIZE, 1, fp);

	free(file);
	free(subDir);
	free(root);
	fclose(fp);

	//check to make sure path exists
	//check that size is > 0
	//check that offset is <= to the file size
	//write data
	//set size (should be same as input) and return, or error

	return size;
}
/*
 * truncate is called when a new file is created (with a 0 size) or when an
 * existing file is made shorter. We're not handling deleting files or
 * truncating existing ones, so all we need to do here is to initialize
 * the appropriate directory entry.
 *
 */
static int cs1550_truncate(const char *path, off_t size)
{
	(void) path;
	(void) size;

    return 0;
}


/*
 * Called when we open a file
 *
 */
static int cs1550_open(const char *path, struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;
    /*
        //if we can't find the desired file, return an error
        return -ENOENT;
    */

    //It's not really necessary for this project to anything in open

    /* We're not going to worry about permissions for this project, but
	   if we were and we don't have them to the file we should return an error

        return -EACCES;
    */

    return 0; //success!
}

/*
 * Called when close is called on a file descriptor, but because it might
 * have been dup'ed, this isn't a guarantee we won't ever need the file
 * again. For us, return success simply to avoid the unimplemented error
 * in the debug log.
 */
static int cs1550_flush (const char *path , struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;

	return 0; //success!
}

/* Thanks to Mohammad Hasanzadeh Mofrad (@moh18) for these
   two functions */
void * cs1550_init(struct fuse_conn_info* fi)
{
		struct cs1550_root_directory *root;
	  (void) fi;
		fp = fopen(".disk", "rb+");

		//find bit map, then read first position to see if root_directory has been
		//allocated or not
		fseek(fp, -3 * BLOCK_SIZE, SEEK_END);
		unsigned char rootByte = 0;
		fread(&rootByte, 1, 1, fp);
		unsigned char rootBit = (rootByte & (1<<(8-1)));

		//always set last 3 bits of bitmap to be three, as these are the
		//bits representing the bitmap itself
		fseek(fp, 0, SEEK_END);

		unsigned char currBitMap = 0;
		fread(&currBitMap, 1, 1, fp);

		unsigned char bitMap = 0x03;
		currBitMap = (currBitMap | bitMap);

		fseek(fp, -1, SEEK_END);
		fwrite(&currBitMap, 1, 1, fp);

		//if root bit is 0, root directory hasn't been initialized yet
		//initialize root directory in bit map
		fseek(fp, 0, SEEK_SET);
		root = malloc(sizeof(struct cs1550_root_directory));
		if(rootBit == 0)
		{
			root->nDirectories = 0;
			//write root directory to disk
			fwrite(root, BLOCK_SIZE, 1, fp);
			fseek(fp, -3 * BLOCK_SIZE, SEEK_END);
			rootBit = 0x80;
			fwrite(&rootBit, 1, 1, fp);
		}

		fclose(fp);
		free(root);
    printf("We're all gonna live from here ....\n");
		return NULL;
}

static void cs1550_destroy(void* args)
{
		(void) args;
    printf("... and die like a boss here\n");
}


//register our new functions as the implementations of the syscalls
static struct fuse_operations hello_oper = {
    .getattr	= cs1550_getattr,
    .readdir	= cs1550_readdir,
    .mkdir	= cs1550_mkdir,
		.rmdir = cs1550_rmdir,
    .read	= cs1550_read,
    .write	= cs1550_write,
		.mknod	= cs1550_mknod,
		.unlink = cs1550_unlink,
		.truncate = cs1550_truncate,
		.flush = cs1550_flush,
		.open	= cs1550_open,
		.init = cs1550_init,
    .destroy = cs1550_destroy,
};

//Don't change this.
int main(int argc, char *argv[])
{
	return fuse_main(argc, argv, &hello_oper, NULL);
}
