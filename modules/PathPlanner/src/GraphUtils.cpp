#include "GraphUtils.h"
#include <stdlib.h>
#include <math.h>

// Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
// intersect the intersection point may be stored in the double i_x and i_y.
bool get_line_intersection(double p0_x, double p0_y, double p1_x, double p1_y, 
			   double p2_x, double p2_y, double p3_x, double p3_y, double *i_x, double *i_y) 
{
    double s1_x, s1_y, s2_x, s2_y;
    s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
    s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

    double s, t;
    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
    t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

    if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
    {
        // Collision detected
        if (i_x != NULL)
            *i_x = p0_x + (t * s1_x);
        if (i_y != NULL)
            *i_y = p0_y + (t * s1_y);
	        
        return true;
    }

    return false; // No collision
}
 
double get_euclidian_distance(double x0, double y0, double x1, double y1){
  return ( sqrt( pow((x0 - x1),2) + pow((y0 - y1),2) ) ); 
} 

int get_manhattan_distance(int x0, int y0, int x1, int y1){
  return ( abs(x1-x0) + abs(y1-y0) );
}


/*
//----------------------- IndexedPriorityQLow ---------------------------
//
//  Priority queue based on an index into a set of keys. The queue is
//  maintained as a 2-way heap.
//
//  The priority in this implementation is the lowest valued key
//------------------------------------------------------------------------


template<class KeyType> void IndexedPriorityQLow::Swap(int a, int b){
  int temp = m_Heap[a]; m_Heap[a] = m_Heap[b]; m_Heap[b] = temp;
  
  //change the handles too
  m_invHeap[m_Heap[a]] = a; m_invHeap[m_Heap[b]] = b;
}

template<class KeyType> void IndexedPriorityQLow::ReorderUpwards(int nd)
{
  //move up the heap swapping the elements until the heap is ordered
  while ( (nd>1) && (m_vecKeys[m_Heap[nd/2]] > m_vecKeys[m_Heap[nd]]) ){      
    Swap(nd/2, nd);
    
    nd /= 2;
  }
}

template<class KeyType> void IndexedPriorityQLow::ReorderDownwards(int nd, int HeapSize) {
  //move down the heap from node nd swapping the elements until
  //the heap is reordered
  while (2*nd <= HeapSize) {
    int child = 2 * nd;
      
    //set child to smaller of nd's two children
    if ((child < HeapSize) && (m_vecKeys[m_Heap[child]] > m_vecKeys[m_Heap[child+1]])) {
      ++child;
    }

    //if this nd is larger than its child, swap
    if (m_vecKeys[m_Heap[nd]] > m_vecKeys[m_Heap[child]]) {
      Swap(child, nd);
      
      //move the current node down the tree
      nd = child;
    }
    else {
      break;
    }
  }
}
  
//you must pass the constructor a reference to the std::vector the PQ
//will be indexing into and the maximum size of the queue.
template<class KeyType> IndexedPriorityQLow::IndexedPriorityQLow(vector<KeyType>& keys,
					 int MaxSize):m_vecKeys(keys),
						      m_iMaxSize(MaxSize),
						      m_iSize(0) {
  m_Heap.assign(MaxSize+1, 0);
  m_invHeap.assign(MaxSize+1, 0);
}

template<class KeyType> bool IndexedPriorityQLow::empty() const { return (m_iSize==0); }

//to insert an item into the queue it gets added to the end of the heap
//and then the heap is reordered from the bottom up.
template<class KeyType> void IndexedPriorityQLow::insert(const int idx) {
  assert (m_iSize+1 <= m_iMaxSize);
  
  ++m_iSize;
  
  m_Heap[m_iSize] = idx;
  
  m_invHeap[idx] = m_iSize;

  ReorderUpwards(m_iSize);
}

//to get the min item the first element is exchanged with the lowest
//in the heap and then the heap is reordered from the top down. 
template<class KeyType> int IndexedPriorityQLow::Pop() {
  Swap(1, m_iSize);
  
  ReorderDownwards(1, m_iSize-1);

  return m_Heap[m_iSize--];
}

//if the value of one of the client key's changes then call this with 
//the key's index to adjust the queue accordingly
template<class KeyType> void IndexedPriorityQLow::ChangePriority(const int idx) {
  ReorderUpwards(m_invHeap[idx]);
}
*/




