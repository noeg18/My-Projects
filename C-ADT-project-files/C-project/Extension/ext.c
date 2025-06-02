#include "ext.h"

dict* dict_init(void)
{
  dict* d = (dict*)calloc(1, sizeof(dict));
  if(d == NULL){
     return NULL;
  }
  d->str = (char*)calloc(MAXSTR, sizeof(char));
  if(d->str == NULL){
     return NULL;
  }
  return d;
}

bool dict_addword(dict* p, const char* wd)
{
   if(p == NULL || wd == NULL){
      return NULL;
   }
   dict* f = p;
   while(f->next != NULL){
      f = f->next;
   }   
   bool wordinlistreturn = iswordinlist(p, wd);
   if(wordinlistreturn){
      return false;
   }
   dict* n = dict_init();
   f->next = n;
   n->up = f;
   n->freq++;
   int l = 0; 
   for(int i = 0; wd[i] != '\0'; i++){
      if(isalpha(wd[i])){
         n->str[l] = tolower(wd[i]);
      }
      else{
         n->str[l] = wd[i];
      }
      l++;
   }
   n->str[l] = '\0';
   
   return true;   
}

bool iswordinlist(dict* p, const char* wd)
{

   while(p->next != 0){
      p = p->next;
      if(strcmp(p->str, wd) == 0){
         p->freq++;
         return true;
      }
   }
   return false;

}
   
int dict_wordcount(const dict* p)
{
   if(p == NULL){
      return 0;
   }

   int wrdcnt = p->freq;
  
   wrdcnt = wrdcnt + dict_wordcount(p->next);
   return wrdcnt;

}

dict* dict_spell(const dict* p, const char* str)
{
   if(p == NULL || str == NULL){
      return NULL;
   }
   
   if(p->next == 0){
      return NULL;
   }
   dict* f = p->next;
   if(strcasecmp(f->str, str) == true){
      return f;
   }
   while(f->next != NULL){
      f = f->next;
      if(strcasecmp(f->str, str) == true){
         return f;
      }
   }
   return NULL;

}

bool strcasecmp(char* str, const char* str2)
{
   int l = 0;
   for(int i = 0; str2[i] != 0; i++){
      if(isalpha(str[i]) && isalpha(str2[i])){
         if(tolower(str[i]) != tolower(str2[i])){
            return false;
         }
      }
      else{
         if(str[i] != str2[i]){
            return false;
         }
      }
      l++;
   }
   if(str[l] != '\0'){
      return false;
   }
   return true;
}

int dict_mostcommon(const dict* p)
{
  if(p == NULL){
      return 0;
   }
     
   int maxfreq = p->freq;

   maxfreq = max(maxfreq, dict_mostcommon(p->next));
   return maxfreq;
}

int max(int x, int y)
{

  if(x > y){
  return x;
  }
  if(y > x){
  return y;
  }

  return x;

}

void dict_free(dict** p)
{
   if(*p == NULL){
      return;
   }

   dict_free(&(*p)->next);
   
   free((*p)->str);
   free(*p);
   *p = NULL;
}   

void test(void)
{

  assert(strcasecmp("bristol", "Bristol"));
  assert(strcasecmp("bristol", "cardiff") == false);
  assert(strcasecmp("BRISTOL", "BristoL"));

  assert(max(5, 10) == 10);
  assert(max(10, 10) == 10);

}
