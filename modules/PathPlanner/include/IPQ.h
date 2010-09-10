#ifndef IPQ_H
#define IPQ_H

#include <vector> 
#include <assert.h>

using namespace std; 


// M.Buckland's indexed priority queue
// it is basically a priority queue, but the ordering of the values are 
// done using the values in another sequence. The reason to use this is
// to keep a list of ordered index of nodes, but the ordering should be
// according to the cost of getting to that node rather than their index
// used in astar.  
//


//----------------------- IndexedPriorityQLow ---------------------------
//
//  Priority queue based on an index into a set of keys. The queue is
//  maintained as a 2-way heap.
//
//  The priority in this implementation is the lowest valued key
//------------------------------------------------------------------------
template<class KeyType>
class IndexedPriorityQLow
{
private:

  vector<KeyType>&  m_vecKeys;

  vector<int>       m_Heap;
 
  vector<int>       m_invHeap;

  int                    m_iSize,
                         m_iMaxSize;

  void Swap(int a, int b)
  {
    int temp = m_Heap[a]; m_Heap[a] = m_Heap[b]; m_Heap[b] = temp;

    //change the handles too
    m_invHeap[m_Heap[a]] = a; m_invHeap[m_Heap[b]] = b;
  }

  void ReorderUpwards(int nd)
  {
    //move up the heap swapping the elements until the heap is ordered
    while ( (nd>1) && (m_vecKeys[m_Heap[nd/2]] > m_vecKeys[m_Heap[nd]]) )
    {      
      Swap(nd/2, nd);

      nd /= 2;
    }
  }

  void ReorderDownwards(int nd, int HeapSize)
  {
    //move down the heap from node nd swapping the elements until
    //the heap is reordered
    while (2*nd <= HeapSize)
    {
      int child = 2 * nd;

      //set child to smaller of nd's two children
      if ((child < HeapSize) && (m_vecKeys[m_Heap[child]] > m_vecKeys[m_Heap[child+1]]))
      {
        ++child;
      }

      //if this nd is larger than its child, swap
      if (m_vecKeys[m_Heap[nd]] > m_vecKeys[m_Heap[child]])
      {
        Swap(child, nd);

        //move the current node down the tree
        nd = child;
      }

      else
      {
        break;
      }
    }
  }


public:
  
  //you must pass the constructor a reference to the vector the PQ
  //will be indexing into and the maximum size of the queue.
  IndexedPriorityQLow(vector<KeyType>& keys,
                      int              MaxSize):m_vecKeys(keys),
                                                m_iMaxSize(MaxSize),
                                                m_iSize(0)
  {
    m_Heap.assign(MaxSize+1, 0);
    m_invHeap.assign(MaxSize+1, 0);
  }

  bool empty()const{return (m_iSize==0);}

  //to insert an item into the queue it gets added to the end of the heap
  //and then the heap is reordered from the bottom up.
  void insert(const int idx)
  {
    assert (m_iSize+1 <= m_iMaxSize);

    ++m_iSize;

    m_Heap[m_iSize] = idx;

    m_invHeap[idx] = m_iSize;

    ReorderUpwards(m_iSize);
  }

  //to get the min item the first element is exchanged with the lowest
  //in the heap and then the heap is reordered from the top down. 
  int Pop()
  {
    Swap(1, m_iSize);

    ReorderDownwards(1, m_iSize-1);

    return m_Heap[m_iSize--];
  }

  //if the value of one of the client key's changes then call this with 
  //the key's index to adjust the queue accordingly
  void ChangePriority(const int idx)
  {
    ReorderUpwards(m_invHeap[idx]);
  }
};


#endif
