# My projects! 

Hi :wave: I am Nagat and welcome to my 'My Projects' repository, in which I will be introducing the most exciting projects I have ever worked on. Most of these projects I have completed as part of my MSc Computer Science (Conversion) at the University of Bristol. Others, I have been working on in my own time to get stuck into the technologies and tools I am most interested in! 

# Current projects 

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
- Takes SQL commands (according to a specific BNF) and stores the resulting tables/databases in a tree (creates database files as needed)
- Changes the data in the tree depending on the command
- Responds based on updated table data

The below shows the server in action:

![java database gif](Java-database-project/db.gif)

It also demonstrates the challenge of ensuring all commands were handled as case-insensitive

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
- How to program rather than just code (first time working towards deliberately ambiguous instructions - difficult but worth it!:tada:)
- It is imporant to be intentional about design and how classes will interact from the beginning 
- However, always be prepared to refactor significantly as you understand the objective more


## Group Project: Used Software Engineering principles to develop and deploy a P5js game (HEX WARS)

[Check out our group's repo](https://github.com/UoB-COMSM0166/2025-group-7)
[Play the game!](https://uob-comsm0166.github.io/2025-group-7/)

I did many things for the first time as part of this project. From working according to an Agile life cycle (taking part in sprints, reviews and retrospectives), Extreme Programming, to using Git in practice. 

### Main personal challenge: Learn how to code in JavaScript and integrate individuals' code

We decided as a team to develop our own individual implementations so we could all get to grips with JavaScript (Which we were all unfamiliar with)
Here is a demonstration of my own prototype that I made when I was first learning the language:




### My main Contributions: 
- End of round and game restart functionality
- Tank destroy animations
- Saw weapon design and functionality
- A bulk of report writing

### What we learned: 
- Focus on sustainability and accessability from the very beginning
- draft design technical challenges earlier in the project






