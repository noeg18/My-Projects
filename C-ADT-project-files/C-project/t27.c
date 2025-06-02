#include "t27.h"

int array_element(char c);
dict* add_letter(dict* p, char c);
dict* nodeofletter(const dict* p, char c);
dict* parent_node(dict* p);
dict* mostcommon_node(dict* p, dict* max, int count);
bool checkstring(const char* wd);
bool repeat(dict* p, const char* wd);
int max(int x, int y);
int checkparent(dict* t, dict* p, int tdistance);

#define MAXSTR 50
#define APOSTROPHE 26 

dict* dict_init(void)
{ 
   dict* d = (dict*)calloc(1, sizeof(dict));
   if(d == NULL){
      return NULL;
   }
   d->freq = 0;
   return d;
}

bool dict_addword(dict* p, const char* wd)
{
   if(p == NULL || wd == NULL){
      return false;
   }
   bool checkstringreturn = checkstring(wd);
   if(checkstringreturn == false){
      return false;
   }
   bool repeatreturn = repeat(p, wd);
   if(repeatreturn){
      return false;
   }

   dict* n = add_letter(p, wd[0]);
   if(wd[1] == '\0'){
      n->terminal = true;
      n->freq++;
   } 
   for(int i = 1; wd[i] != '\0'; i++){
      n = add_letter(n, wd[i]);
      if(wd[i+1] == '\0'){
         n->terminal = true;
         n->freq++;
      }
   }
   return true;
}

bool repeat(dict* p, const char* wd)
{  
   if(p == NULL){
      return false;
   }
   for(int i = 0; wd[i] != 0; i++){
      p = nodeofletter(p, wd[i]);
      if(p == NULL){
         return false;
      }
      if(p->terminal == true && wd[i+1] == '\0'){
         p->freq++;
         return true;
      }
   }   
   return false;
}

bool checkstring(const char* wd)
{

   for(int i = 0; wd[i] != '\0'; i++){
      if(isalpha(wd[i]) == 0 && wd[i] != '\''){
         return false;
      }
   }
   return true;
}   

dict* add_letter(dict* p, char c)
{ 
   if(p->dwn[array_element(c)] == 0){
      dict* n = dict_init();
      p->dwn[array_element(c)] = n;
      n->up = p;
      return n;
   }
   
   return p->dwn[array_element(c)];
} 


int array_element(char c)
{
   if(isupper(c)){
      return c - 'A';
   }
   if(islower(c)){
      return c - 'a';
   } 
   else{
      return APOSTROPHE;
   }
}

int dict_nodecount(const dict* p)
{
   if(p == NULL){
      return 0;
   }
   int nodecnt = 1;

   for(int i = 0; i < ALPHA; i++){
      nodecnt = nodecnt +  dict_nodecount(p->dwn[i]);
   }
   return nodecnt;
}

int dict_wordcount(const dict* p)
{
   if(p == NULL){
      return 0;
   }

   int wrdcnt = p->freq;
  
   for(int i = 0; i < ALPHA; i++){
      wrdcnt = wrdcnt + dict_wordcount(p->dwn[i]);

   }
   return wrdcnt;

}
   
dict* dict_spell(const dict* p, const char* str)
{
   if(p == NULL || str == NULL){
      return NULL;
   }   
   dict* n = nodeofletter(p, str[0]);
   if(n == NULL){
      return NULL;
   }
   if(n->terminal == true && str[1] == '\0'){
      return n; 
   }
   for(int i = 1; str[i] != '\0'; i++){
      n = nodeofletter(n, str[i]);
       
      if(n == NULL){
         return NULL;
      } 
      if(n->terminal == true && str[i+1] == '\0'){
         return n;
      }
   }
   return NULL;
}



dict* nodeofletter(const dict* p, char c)
{
   if(p == NULL){
      return NULL;
   }
   if(p->dwn[array_element(c)] == NULL ||
      p->dwn[array_element(c)] == 0){
      return NULL;
   }
   return p->dwn[array_element(c)];
}
      
      

int dict_mostcommon(const dict* p)
{
   if(p == NULL){
      return 0;
   }
     
   int maxfreq = p->freq;

   for(int i = 0; i < ALPHA; i++){
      if(p->dwn[i] != 0){
         maxfreq = max(maxfreq, dict_mostcommon(p->dwn[i]));
      } 
   }
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

unsigned dict_cmp(dict* p1, dict* p2)
{ 
   unsigned int distance = 0;  

   dict* parent1 = parent_node(p1);
   distance++;
   if(parent1 == p2){
      return distance;
   }
   //check one is not the parent of the other
   dict* temp = parent1;
   unsigned int tdistance = distance;
   tdistance = checkparent(temp, p2, tdistance);
   if(tdistance > 0){
      return tdistance;
   }
   dict* parent2 = parent_node(p2);
   distance++;
   if(parent1 == parent2){
      return distance;
   }
   temp = parent2;
   unsigned int tdistance2 = distance - 1;
   tdistance2 = checkparent(temp, p1, tdistance2);
   if(tdistance2 > 0){
      return tdistance2;
   }
   //calculate distance to root from both nodes 
   while(parent1 != parent2){
      if(parent1->up != 0){
         parent1 = parent_node(parent1);
         distance++;
      }
      if(parent2->up != 0){
         parent2 = parent_node(parent2);
         distance++;
      }
   }
   return distance;
} 

int checkparent(dict* t, dict* p, int tdistance)
{
   while(t->up != 0){
      t = parent_node(t);
      tdistance++;
      if(t == p){
         return tdistance;
      }
   }
    return 0;
}

dict* parent_node(dict* p)
{
   if(p == NULL){
      return NULL;
   }
   return p->up;
}

void dict_autocomplete(const dict* p, const char* wd, char* ret)
{
   // find last node of word
   dict* s = nodeofletter(p, wd[0]);
   for(int i = 1; wd[i] != '\0'; i++){
      s = nodeofletter(s, wd[i]);
   }
 
   dict* max = dict_init();
   dict* t = NULL;
   int count = 0;
   t = mostcommon_node(s, max, count);
   free(max); 
   char str[MAXSTR];
   int l = 0;
   dict* temp = NULL;
   while(t != s){
      temp = t->up;
      for(int i = 0; i < ALPHA; i++){
         if(temp->dwn[i] == t){
            str[l] = 'a' + i;
            l++;
         }
      }
      t = parent_node(t);
   }
   str[l] = '\0';

   l = 0;
   for(int j = strlen(str) - 1; j >= 0; j--){
       ret[l] = str[j];
       l++;
   }
   ret[l] = '\0';
}


dict* mostcommon_node(dict* p, dict* max, int count)
{ 
   
   if(p == NULL){
      return NULL;
   }

   if(p->freq > max->freq && count != 0){
      max = p;
   }
   count++;

   for(int i = 0; i < ALPHA; i++){
      if(p->dwn[i] != 0){
         max = mostcommon_node(p->dwn[i], max, count);
      }
  }
  return max;

}

void dict_free(dict** p)
{
   if(*p == NULL){
      return;
   }
       
   for(int i = 0; i < ALPHA; i++){
      dict_free(&((*p)->dwn[i]));       
   }
   free(*p);
   *p = NULL;
}

void test(void)
{

  assert(array_element('A') == 0);
  assert(array_element('m') == 12);
  assert(array_element('\'') == 26);

  dict* d = NULL;

  d = dict_init();

  assert(dict_addword(d, "cat"));
  dict* n = dict_spell(d, "cat");
  n = parent_node(n);
  assert(parent_node(n));

  assert(checkstring("...") == false);
  assert(checkstring("hello"));
  assert(checkstring("didn't"));

  assert(repeat(d, "cat"));
  assert(repeat(d, "dog") == false);

  assert(max(2, 10) == 10);
  assert(max(5, 5) == 5);

  dict_free(&d);
}






