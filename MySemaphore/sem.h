#include <linux/sched.h>
struct Node
{
  struct task_struct* data;
  int priority;
  struct Node* next;
};
struct cs1550_sem
{
  int value;
  struct Node* head;
};
