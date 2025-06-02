#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>
#include <ctype.h>

#define MAXSTR 50

//singly linked list

struct dict {

   char* str;

   struct dict* next;

   struct dict* up;

   int freq;

};
typedef struct dict dict;


dict* dict_init(void);
bool dict_addword(dict* p, const char* wd);
int dict_wordcount(const dict* p);
dict* dict_spell(const dict* p, const char* str);
void dict_free(dict** p);
bool iswordinlist(dict* p, const char* wd);
int dict_mostcommon(const dict* p);
int max(int x, int y);
bool strcasecmp(char* str, const char* str2);
void test(void);

