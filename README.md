# My projects!

# Current projects 

Hi :wave: I am Nagat and Welcome to my 'My Projects' repository, in which I will be introducing the most exciting projects I have ever worked on. Most of these projects I have completed as part of my MSc Computer Science (Conversion) at the University of Bristol. Others, I have been working on in my own time to get stuck into the technologies and tools I am most interested in! 

Let's start with the most sizeable one:

## Dissertation/Individual Summer Project: "Assessing Correctness in LLM-Based Code Generation" :books:

I have chosen to tackle this topic for my summer project because I am passionate about ensuring emerging technologies are safe and reliable. I believe that investigating the accuracy of LLMs will be a great foundation in understanding their current state before potentially looking into societal implications in future projects. 

### What I have done so far 

I have read many papers around the topic to understand how accuracy has been evaluated in the past:


### Next Steps

1. Learn how to extract log probabilities for tokens
2. Choose an evaluation technique

*Watch this space!* I will be working on this project until September 2025 so prepare for this summary to develop immensely :star_struck:


## Building a contextual chatbot framework using Tensorflow

I am not only interested in investigating how well these models work but also how they are built under the hood. That is why I have taken on the challenge of building a chatbot in my own time. I also want to put my python skills to the test. It is already proving to be very interesting!

### What I have done so far:

1. Intent definitions -> Tensorflow model
 - Created classes defining conversational intents (holding tags, patterns and responses)
 - Created classes of words within documents (learned about stemmed words and how they make up sentences)
 - Shuffled and split this training data to mitigate bias

2. Build chatbot framework
 - Used bag-of-words method to tokenise user input

### Next steps

Finish building, train and test the model 


# Past projects


## Built a relational database server in Java that handles SQL commands

### What it does: 
-Takes SQL commands (according to a specific BNF) and stores the resulting tables/databases in a tree (creates database files as needed)
-Changes the data in the tree depending on the command
-Responds based on updated table data

![java database gif](Java-database-project/db.gif)

The above demonstrates the challenge of ensuring all commands were handled as case-insensitive

### Main challenge: 
Making sure files and data in tree both updated correctly, specifically for more challenging commands e.g. update

The below function was useful in finding the index of the child (element within a table) to update it cleanly

```
    public int getChildNumber(DBTreeNode parent, DBTreeNode child){
        for(int i = 0 ; i < next.size() ; i++) {
            if (parent.next.get(i) == child) {
                return parent.next.indexOf(child);
            }
        }
        return 0;
    }
```

I wrote the below function in order to traverse through the tree deleting all children after recieving a 'DELETE' command
```
    public void deleteAllChildren(DBTreeNode root){
        if(root == null){
            return;
        }
        for(int i = 0 ; i < root.next.size() ; i++) {
            deleteAllChildren(root.next.get(i));
        }
        parent.next.remove(root);
    }
```


### What I learned: 
-How to program rather than just code (first time working towards deliberately ambiguous instructions - difficult but worth it!)
-It is imporant to be intentional about design and how classes will interact from the beginning 
-However, always be prepared to refactor significantly as you understand the objective more






