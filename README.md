CSARCH S12 Group 11 

Group Members:
DATARIO, Yasmin Audrey
EDRALIN, Philippe Nikkos Keyan
MENDOZA, Antonio Gabriel

Type of cache memory: 8-way BSA + random replacement algorithm
An 8-way Block Set Associative cache with a random replacement algorithm is a specific configuration of cache memory, oftentimes best explained as the middle ground of direct-mapping and full associative. In this design, the cache is divided into multiple sets, and each set can contain up to 8 blocks. When a processor accesses data, the cache determines which set the data could be in based on its memory address and then checks only those 8 blocks within that set, reducing the search space compared to a fully associative cache while avoiding the strict one-to-one mapping of a direct-mapped cache. The random replacement algorithm, applied when a new block needs to be loaded into a full set, randomly selects one of these blocks for replacement. 


Detailed Analysis of the three (3) test cases:
This specific 8-way Block Set Associative cache configuration, equipped with a random replacement algorithm, features a total of 32 blocks, divided into 4 sets, with each set capable of holding 8 blocks. Each block contains 64 words, and the cache operates on a load-through read policy.

`1. Sequential sequence: up to 2n cache blocks. Repeat the sequence four times. Example: 0,1,2,3,…,2n-1 {4x}`

In this 8-way set associative cache configuration, each initial access from 0 to 31 results in a cache miss, as the cache begins in an empty state. The blocks are distributed evenly across the four sets, with a sequential mapping where each consecutive block address is assigned to the next set in a round-robin fashion. For instance, block 0 is stored in set 0, block 1 in set 1, and so on, cycling back so that block 4 is also in set 0. By the time all MM 32 blocks (0 to 31) are filled, each set contains a sequence of blocks spaced four apart, like 0, 4, 8, etc., in set 0. However, this initial distribution is only the beginning. Once the cache is full, any further accesses to new memory addresses will result in cache misses, invoking the random replacement algorithm. The snapshot of the entire cache will look like:


|       | Block 0 | Block 1 | Block 2 | Block 3 | Block 4 | Block 5 | Block 6 | Block 7 |
|-------|---------|---------|---------|---------|---------|---------|---------|---------|
| Set 0 | 0       | 4       | 8       | 12      | 16      | 20      | 24      | 28      |
| Set 1 | 1       | 5       | 9       | 13      | 17      | 21      | 25      | 29      |
| Set 2 | 2       | 6       | 10      | 14      | 18      | 22      | 26      | 30      |
| Set 3 | 3       | 7       | 11      | 15      | 19      | 23      | 27      | 31      |

Once the cache is fully populated with memory blocks 0 to 31, further access to new memory addresses (starting from 32) results in cache misses due to the cache being at capacity. The random replacement algorithm comes into play at this point. This algorithm, rather unpredictable by nature, selects any block within a set for replacement, without any reference to the sequence of access or the frequency of use of the blocks. For example, when memory block 32 is accessed, it randomly replaces a block in the designated set, such as replacing block 28 in set 0, block 7. This random replacement continues for each new memory block accessed, up to block 63. The outcome of this process is a final cache state where the blocks present in each set are a result of random replacements rather than a reflection of the sequential access pattern.

The random nature of this replacement strategy significantly influences cache performance. As the sequence from 0 to 63 is repeated, the cache hit or miss for each access depends on whether the randomly replaced blocks from the previous sequence are still present in the cache. In this specific scenario, the cache registers 37 hits and 219 misses across the entire sequence, culminating in a miss rate of 85.546875%. This high miss rate underscores the mismatch between the sequential nature of the access pattern and the unpredictability of the random replacement algorithm, leading to frequent cache misses and a lower than optimal cache efficiency. The final snapshot of the cache shows a mix of memory blocks, illustrating the result of the random replacements over the course of the access sequence.

|       | Block 0 | Block 1 | Block 2 | Block 3 | Block 4 | Block 5 | Block 6 | Block 7 |
|-------|---------|---------|---------|---------|---------|---------|---------|---------|
| Set 0 | 60      | 56      | 52      | 28      | 24      | 44      | 12      | 48      |
| Set 1 | 17      | 61      | 37      | 45      | 41      | 33      | 53      | 57      |
| Set 2 | 42      | 2       | 54      | 50      | 58      | 62      | 34      | 26      |
| Set 3 | 27      | 39      | 51      | 11      | 63      | 55      | 59      | 35      |

The impact of the cache's performance on memory access times is evident in the statistics we got from the application. The average memory access time is calculated to be 955.46875 nanoseconds. This figure is relatively high and is influenced by the cache's high miss rate. Each cache miss incurs additional time as the system retrieves the data from the main memory, which is significantly slower than accessing it from the cache. This longer access time for cache misses is a critical factor contributing to the elevated average memory access time.

Moreover, the total memory access time across all accesses is 142,747.0 nanoseconds. This cumulative figure underscores the time cost of accessing memory in the presence of a high cache miss rate. Given that there are 256 memory accesses (as indicated by the memory access count) and a high proportion of these are cache misses, the total memory access time escalates accordingly.

`2. Random sequence: containing 4n blocks. `

In this test case sequence, we used the random integer function of Java to populate our memory block that does not exceed 128 (4n). For the 8-way BSA configuration, it will first fill the blocks with no data and we can also notice that since we used randomly generated numbers, there will be some hits on the initialization phase. 

|       | Block 0 | Block 1 | Block 2 | Block 3 | Block 4 | Block 5 | Block 6 | Block 7 |
|-------|---------|---------|---------|---------|---------|---------|---------|---------|
| Set 0 | 104     | 112     | 120     | 4       | 108     | 76      | 116     | 64      |
| Set 1 | 37      | 69      | 29      | 45      | 53      | 5       | 41      | 125     |
| Set 2 | 14      | 118     | 126     | 6       | 98      | 18      | 82      | 30      |
| Set 3 | 111     | 91      | 83      | 31      | 3       | 119     | 67      | 87      |

After populating the memory blocks, any replacement that will occur to the memory block will be handled by the random replacement algorithm made by the group. After doing the BSA configuration to all of the blocks up to 128, the total Hit Count gathered is 28 while the Miss Count is 100 making the Catch Hit Rate  =  21.875% and Catch Miss Rate = 78.125%.

|       | Block 0 | Block 1 | Block 2 | Block 3 | Block 4 | Block 5 | Block 6 | Block 7 |
|-------|---------|---------|---------|---------|---------|---------|---------|---------|
| Set 0 | 4       | 112     | 120     | 116     | 48      | 96      | 44      | 20      |
| Set 1 | 37      | 9       | 29      | 113     | 65      | 109     | 53      | 125     |
| Set 2 | 14      | 98      | 106     | 18      | 86      | 122     | 118     | 90      |
| Set 3 | 23      | 91      | 51      | 7       | 27      | 119     | 47      | 111     |

As we can see the RLU influenced the Hit Rate and Miss Rate of the memory blocks so we don’t really know if it will be high or low. We should also consider the randomly generated numbers that we made in this sequence. The average memory access time for this is 881.25 nanoseconds. Which is highly influenced by the algorithm and the generated numbers. The total memory access time for this is 65892.0 nanoseconds.

`3. Mid-repeat blocks: Start at block 0, repeat the sequence in the middle two times up to n-1 blocks, after which continue up to 2n. Then, repeat the sequence four times. Example: if n=8, sequence=0, 1,2,3,4,5,6,1,2,3,4,5,6, 7,8,9,10,11,12,13,14,15 {4x} `

At the onset, each access from 0 to 30 leads to a cache miss, a typical scenario in a cold cache start. These blocks are methodically assigned across the four sets in a round-robin manner, ensuring an even distribution. For example, block 0 is placed in set 0, block 1 in set 1, continuing in this fashion until block 4 is again placed in set 0. Once all 31 blocks (0 to 30) are populated, each set contains blocks that are evenly spaced by four, such as blocks 0, 4, 8, etc., in set 0.

|       | Block 0 | Block 1 | Block 2 | Block 3 | Block 4 | Block 5 | Block 6 | Block 7 |
|-------|---------|---------|---------|---------|---------|---------|---------|---------|
| Set 0 | 0       | 4       | 8       | 12      | 16      | 20      | 24      | 28      |
| Set 1 | 1       | 5       | 9       | 13      | 17      | 21      | 25      | 29      |
| Set 2 | 2       | 6       | 10      | 14      | 18      | 22      | 26      | 30      |
| Set 3 | 3       | 7       | 11      | 15      | 19      | 23      | 27      | -       |


Afterwards, the pattern will repeat up to 2(n) which is block 63. That being said, blocks 1 to 30 will hit and 31 will occupy set 3 block 7. Upon filling the cache with blocks 0 to 31, any new memory access, starting from block 32, results in a cache miss due to the cache's full capacity. The random replacement algorithm then becomes a pivotal factor. This algorithm randomly selects any block within a set to replace, with no regard to the sequence of access or usage frequency. For instance, accessing block 32 might lead to the replacement of block 4 in set 0, block 1 which is what was shown in the current example. This pattern of random replacement persists for each new memory block accessed, up to block 63. As we complete an iteration, we will continue to repeat the pattern until the 4th iteration. That being said, the hits will depend on which blocks were not replaced by blocks afterwards. The final state of the cache reflects the outcome of these random replacements, rather than mirroring the sequential access pattern as we also saw in test case 1.

|       | Block 0 | Block 1 | Block 2 | Block 3 | Block 4 | Block 5 | Block 6 | Block 7 |
|-------|---------|---------|---------|---------|---------|---------|---------|---------|
| Set 0 | 28      | 48      | 56      | 32      | 52      | 60      | 0       | 16      |
| Set 1 | 53      | 45      | 49      | 61      | 9       | 17      | 13      | 57      |
| Set 2 | 6       | 54      | 18      | 22      | 50      | 58      | 62      | 14      |
| Set 3 | 51      | 55      | 47      | 59      | 7       | 19      | 63      | 3       |

The final state of the cache, as shown in the snapshot, substantiates the random nature of the replacement algorithm. Unlike FIFO (First-In, First-Out) or LIFO (Last-In, First-Out), where blocks are replaced in a predictable order, the random algorithm leads to a more chaotic and less predictable state. 

The total number of memory accesses during the test was 376, reflecting the sequence's repetitive nature. Out of these, only 106 were cache hits, indicating that the requested data was found in the cache about 28.191% of the time. This hit rate is relatively low, suggesting that the cache was not very effective in storing the most frequently accessed data for this particular sequence. In contrast, there were 270 cache misses, accounting for a high miss rate of approximately 71.809%. This indicates that most memory accesses resulted in the system fetching data from the slower main memory, significantly impacting the efficiency of data retrieval.

The average memory access time, calculated at 818.085 nanoseconds, further underscores the inefficiency. This figure includes the latency of both cache hits, which are comparatively faster, and misses, which are slower due to the need to access main memory. The cumulative impact of these misses is evident in the total memory access time for the test, which amounted to a substantial 179854.0 nanoseconds.




