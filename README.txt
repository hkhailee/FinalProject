README
LAB-04
Team Programming Project: Bioinformatics
Marcus Henke 
Dominik Huffield
Hailee Kiesecker

Due date: 12pm 12/07/2018

Layout of the B-Tree file on disk:

The layout of our btree binary disk file is as follows: Each Node on the disk consists of a Location; number of objects; isLeaf; Parent pointer; Child pointers;Tree Object with the DNA long strand and the frequency of it occuring. after one node is done being given then the next one follows. 

Node
------------------------------------------------------------------------------------
| Location size int of four bytes;                                                |
| Number of objects size int, Four Bytes;                                         |
| isLeaf meanining the node has no children size int, four bytes;                 |
| parent Pointer size int, four bytes;                                            |
| Child pointer array, each object in the array is size int, four bytes;          |
| TreeeObject having both the long DNA strand and the frequency of it occuring:   |
| DNA strand size long, eight bytes;                                              |
| Frequncy size int, four bytes;                                                  |
------------------------------------------------------------------------------------

Objservations:

The speed when inserting the long objects into the Binary Tree is fairly slow, the longer the file size the slower it is. This could be due to how fast the bufferreader is taking in objects or it could be from the runtime of our insert method. A secondary objservation is how fast the Binary Search Tree is formed if you use a cache to help create the tree. It makes it so that you do not need to travers the tree every single time to insert a node if the node already exists in the cache. 

A minor observation with our particular tree is that we split the node before insertion if the degree with insertion of an additional object is met. This makes for a cleaner and more organized way of creating the tree. The Binary Tree is organized so that the lowest value of the tree is at the bottom left child. 

