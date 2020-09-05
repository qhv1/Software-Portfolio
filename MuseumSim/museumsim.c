#include <sys/mman.h>
#include <linux/unistd.h>
#include <stdio.h>
#include <sys/resource.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

//#include "unistd.h"
#include "sem.h"

void down(struct cs1550_sem *sem) {
  syscall(__NR_cs1550_down, sem);
}

void up(struct cs1550_sem *sem) {
  syscall(__NR_cs1550_up, sem);
}


struct shared_mem
{
  struct cs1550_sem guideWaitingOnVisitors;
  struct cs1550_sem guideArrivalSem;
  struct cs1550_sem visitorArrivalSem;
  struct cs1550_sem visitorWaitSem;
  struct cs1550_sem guideWaitSem;
  struct cs1550_sem visitorWaitingOnGuides;
  struct cs1550_sem raceCondVisitorLeaving;
  struct cs1550_sem extraVisitors;
  struct cs1550_sem extraGuides;

  struct timeval initTime;

  int visitorsLeft;
  int visitorsArrived;
  int visitorsInside;
  int visitorsWaiting;

  int visitorSeed;
  int guideSeed;

  int visitorCount;
  int guideCount;

  int visitorWait;
  int guideWait;

  int visitorProb;
  int guideProb;

  int visitorDelay;
  int guideDelay;
};
void tourGuideLeaves(struct shared_mem *shared, int threadID)
{
  //down(&shared->guideWaitSem);
  shared->visitorWaitingOnGuides.value = 0;
  shared->visitorWait = 0;
  shared->visitorsArrived = 0;
  shared->guideWait--;
  int i;
  for(i = 0; i < 10; i++)
    up(&shared->visitorArrivalSem);

  if(shared->guideWait == 0)
  {
    //printf("fuckfuckfuckfuckfuck\n");
    //fflush(stdout);
    up(&shared->guideArrivalSem);
    shared->visitorArrivalSem.value = 0;
    for(i = 0; i < 10 && shared->extraVisitors.value != 0; i++)
    {
      up(&shared->extraVisitors);
      shared->visitorsWaiting--;
    }
  }
  else
    shared->guideArrivalSem.value++;


  struct timeval curr;
  gettimeofday(&curr, NULL);
  float time = -1.0*(shared->initTime.tv_sec - curr.tv_sec + ((shared->initTime.tv_usec - curr.tv_usec) / 1000000.0));
  printf("Tour Guide %d leaves the museum time %f.\n",threadID, time);
  fflush(stdout);
  if(shared->guideWait == 0)
  {
    printf("The museum is empty\n");
    fflush(stdout);
    up(&shared->guideWaitSem);
  }
  //up(&shared->guideWaitSem);
}
void visitorLeaves(struct shared_mem *shared, int threadID)
{
  struct timeval curr;
  gettimeofday(&curr, NULL);
  float time = -1.0*(shared->initTime.tv_sec - curr.tv_sec + ((shared->initTime.tv_usec - curr.tv_usec) / 1000000.0));
  printf("Visitor %d leaves the museum at time %f.\n",threadID, time);
  fflush(stdout);
  down(&shared->visitorWaitSem);
  shared->visitorsInside--;
  if(shared->visitorsInside == 0 && shared->guideWait == 1)
  {
    down(&shared->guideWaitSem);
    up(&shared->guideWaitingOnVisitors);
    //up(&shared->guideWaitSem);
  }
  else if(shared->visitorsInside == 0 && shared->guideWait == 2)
  {
    down(&shared->guideWaitSem);
    up(&shared->guideWaitingOnVisitors);
    up(&shared->guideWaitingOnVisitors);
    down(&shared->guideWaitSem);
    up(&shared->extraGuides);
    up(&shared->guideWaitSem);
  }
  up(&shared->visitorWaitSem);

}
void tourMuseum(struct shared_mem *shared, int threadID)
{
  struct timeval curr;
  gettimeofday(&curr, NULL);
  float time = -1.0*(shared->initTime.tv_sec - curr.tv_sec + ((shared->initTime.tv_usec - curr.tv_usec) / 1000000.0));
  printf("Visitor %d tours the museum at time %f.\n",threadID, time);
  fflush(stdout);
  sleep(2);
}
void openMuseum(struct shared_mem *shared, int threadID)
{
  struct timeval curr;
  gettimeofday(&curr, NULL);
  float time = -1.0*(shared->initTime.tv_sec - curr.tv_sec + ((shared->initTime.tv_usec - curr.tv_usec) / 1000000.0));
  printf("Tour Guide %d opens the museum for tours at time %f.\n",threadID, time);
  fflush(stdout);
}
void visitorArrives(struct shared_mem *shared, int threadID)
{
  struct timeval curr;
  gettimeofday(&curr, NULL);
  float time = -1.0*(shared->initTime.tv_sec - curr.tv_sec + ((shared->initTime.tv_usec - curr.tv_usec) / 1000000.0));
  printf("Visitor %d arrives at time %f.\n",threadID, time);
  //shared->visitorsInside++;
  fflush(stdout);
  //up(&shared->visitorArrivalSem);
}
void tourguideArrives(struct shared_mem *shared, int threadID)
{
  struct timeval curr;
  gettimeofday(&curr, NULL);
  float time = -1.0*(shared->initTime.tv_sec - curr.tv_sec + ((shared->initTime.tv_usec - curr.tv_usec) / 1000000.0));
  printf("Tour Guide %d arrives at time %f.\n",threadID, time);
  fflush(stdout);
}


int main(int argc, char** argv)
{
  struct shared_mem *shared = mmap(NULL,sizeof(struct shared_mem),
      PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  gettimeofday(&shared->initTime, NULL);
  float initTime = (shared->initTime.tv_sec) + (shared->initTime.tv_usec / 1000000.0);
  shared->visitorSeed = initTime;
  shared->guideSeed = initTime;

  shared->visitorsInside = 0;
  shared->visitorsArrived = 0;
  shared->visitorsWaiting = 0;

  shared->extraGuides.value = 0;
  shared->raceCondVisitorLeaving.value = 1;
  shared->extraVisitors.value = 0;
  shared->guideWaitingOnVisitors.value = 0;
  shared->visitorWaitingOnGuides.value = 0;

  shared->visitorProb = 70;
  shared->guideProb = 0;

  shared->visitorDelay = 1;
  shared->guideDelay = 12;

  shared->visitorWaitSem.value = 1;
  shared->guideWaitSem.value = 1;

  shared->guideArrivalSem.value = 2;
  shared->visitorArrivalSem.value = 0;

  shared->visitorCount = 11;
  shared->guideCount = 2;

  shared->visitorWait = 0;
  shared->guideWait = 0;

  int i;
  for(i = 1; i < argc; i++) //command line arguments reader
  {
    if(strcmp(argv[i], "-m") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->visitorCount = atoi(argv[i + 1]);
      i++;
    }
    else if(strcmp(argv[i], "-k") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->guideCount = atoi(argv[i + 1]);
      i++;
    }
    else if(strcmp(argv[i], "-pv") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->visitorProb = atoi(argv[i + 1]);
      i++;
    }
    else if(strcmp(argv[i], "-dv") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->visitorDelay = atoi(argv[i+1]);
      i++;
    }
    else if(strcmp(argv[i], "-sv") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->visitorSeed = atoi(argv[i+1]);
      i++;
    }
    else if(strcmp(argv[i], "-pg") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->guideProb = atoi(argv[i+1]);
      i++;
    }
    else if(strcmp(argv[i], "-dg") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->guideDelay = atoi(argv[i+1]);
      i++;
    }
    else if(strcmp(argv[i], "-sg") == 0)
    {
      if(i + 1 <= argc - 1)
        shared->guideSeed = atoi(argv[i+1]);
      i++;
    }
    else
    {
      printf("Invalid argument\n");
      fflush(stdout);
      exit(0);
    }
  }
  printf("The museum is now empty.\n");
  fflush(stdout);

  srand(shared->visitorSeed);
  int pid = fork();
  if(pid == 0) //visitor creation
  {
    for(i = 0; i < shared->visitorCount; i++)
    {
      pid = fork();
      if(pid == 0)
      {
        //printf("generated visitor %d\n", i+1);
        //fflush(stdout);

        if(shared->visitorsArrived == 10 && shared->guideWait == 1)
        {
          //down(&shared->visitorWaitSem);
          //printf("visitor %d PUT IN EXTRAS!!!!!!!!!!!!!!\n", i+1);
          //fflush(stdout);
          shared->visitorsWaiting++;
          up(&shared->visitorWaitSem);
          down(&shared->extraVisitors);
        }
        else if(shared->visitorsArrived == 20 && shared->guideWait == 2)
        {
          //down(&shared->visitorWaitSem);
          //printf("visitor %d PUT IN EXTRAS!!!!!!!!!!!!!!\n", i+1);
          //fflush(stdout);
          shared->visitorsWaiting++;
          up(&shared->visitorWaitSem);
          down(&shared->extraVisitors);
        }

        down(&shared->visitorWaitSem);
        if(shared->visitorWait == 0 && shared->visitorsInside == 0)
        {
          up(&shared->guideWaitSem);
          //printf("generated visitor %d waiting\n", i+1);
          //fflush(stdout);
          //printf("visitorWaitSem value: %d\n", shared->visitorWaitSem.value);
          //fflush(stdout);
          down(&shared->visitorWaitSem);
          //printf("generated visitor %d woken up\n", i+1);
          //fflush(stdout);

          shared->visitorWait++;
        }
        up(&shared->visitorWaitSem);
        //printf("GOING IN visitorArrivalSem value: %d with visitor %d\n", shared->visitorArrivalSem.value,i+1);
        //fflush(stdout);
        down(&shared->visitorArrivalSem);
        //printf("GOING OUT visitorArrivalSem value: %d with visitor %d\n", shared->visitorArrivalSem.value,i+1);
        //fflush(stdout);
        down(&shared->visitorWaitSem);
        if(shared->guideWait == 0)
        {
          //printf("FUCKKKKKKK %d\n", shared->visitorWaitSem.value);
          //fflush(stdout);
          up(&shared->guideWaitSem);
          down(&shared->visitorWaitSem);
        }
        up(&shared->visitorWaitSem);
        down(&shared->visitorWaitSem);
        shared->visitorsInside++;
        shared->visitorsArrived++;
        up(&shared->visitorWaitSem);

        visitorArrives(shared, i+1);

        down(&shared->visitorWaitingOnGuides);
        up(&shared->visitorWaitingOnGuides);
        tourMuseum(shared, i+1);
        visitorLeaves(shared, i+1);
        exit(0);
      }
      else
      {
        //printf("inside else condition\n");
        //fflush(stdout);
        int value = rand() % 100 + 1;
        if(value > shared->visitorProb)
          sleep(shared->visitorDelay);
      }
    }
    for(i = 0; i < shared->visitorCount; i++)
      wait(NULL);
    exit(0);
  }
  else //guide creation
  {
    srand(shared->guideSeed);
    for(i = 0; i < shared->guideCount; i++)
    {
      int pid2 = fork();
      if(pid2 == 0)
      {
        //printf("generated guide %d\n", i+1);
        //fflush(stdout);
        if(shared->guideWait >= 2)
        {
          down(&shared->extraGuides);
          //printf("GHALSHGAHLGHLASHG\n");
          //fflush(stdout);
        }
        down(&shared->guideArrivalSem);
        down(&shared->guideWaitSem);
        if(shared->guideWait == 0)
        {
          up(&shared->visitorWaitSem);
          //printf("generated guide %d waiting\n", i+1);
          //fflush(stdout);
          //printf("guideWaitSem value: %d\n", shared->guideWaitSem.value);
          //fflush(stdout);
          down(&shared->guideWaitSem);
          //printf("generated guide %d woken up\n", i+1);
          //fflush(stdout);
          shared->guideWait++;
          int j;
          for(j = 0; j < 10; j++)
            up(&shared->visitorArrivalSem);
        }
        else if(shared->guideWait == 1 && shared->visitorsInside >=1)
        {
          int j;
          for(j = 0; j < 10; j++)
            up(&shared->visitorArrivalSem);
          shared->guideWait++;
          for(j = 0; j < 10 && shared->extraVisitors.value != 0; j++)
          {
            up(&shared->extraVisitors);
            shared->visitorsWaiting--;
          }
        }
        up(&shared->guideWaitSem);
        tourguideArrives(shared, i+1);
        openMuseum(shared, i+1);
        int j;
        up(&shared->visitorWaitingOnGuides);
        down(&shared->guideWaitingOnVisitors);
        tourGuideLeaves(shared, i+1);
        exit(0);
      }
      else
      {
        int value = rand() % 100 + 1;
        if(value > shared->guideProb)
          sleep(shared->guideDelay);
      }
    }
  }
  for(i = 0; i < shared->visitorCount; i++)
    wait(NULL);
  wait(NULL);
  return 1;
}
