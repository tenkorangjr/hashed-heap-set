import java.util.Comparator;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class HashedHeapSet<T> implements PriorityQueue<T> {

    // by default, we'll build a min-heap: that means the smallest item
    // according to the Comparator will be stored in heap[1] (the root)
    private T[] heap;
    private int size;
    private Comparator<T> comparator;
    /**
     * This HashMap should map each item of type T to the index it's sitting in.
     */
    private HashMap<T, Integer> indexMapping;

    public HashedHeapSet(Comparator<T> comparator, boolean maxHeap) {
        if (comparator != null) {
            this.comparator = comparator;
        } else {
            this.comparator = new Comparator<T>() {

                @Override
                public int compare(T o1, T o2) {
                    return ((Comparable<T>) o1).compareTo(o2);
                }

            };
        }

        if (maxHeap) {
            this.comparator = new Comparator<T>() {

                @Override
                public int compare(T o1, T o2) {
                    return HashedHeapSet.this.comparator.compare(o2, o1);
                }

            };
        }
        size = 0;
        heap = (T[]) new Object[16];
        indexMapping = new HashMap<>();
    }

    public HashedHeapSet(Comparator<T> comparator) {
        this(comparator, false);
    }

    public HashedHeapSet(boolean maxHeap) {
        this(null, maxHeap);
    }

    public HashedHeapSet() {
        this(null, false);
    }

    private int left(int index) {
        return index * 2;
    }

    private int right(int index) {
        return index * 2 + 1;
    }

    private int parent(int index) {
        return index / 2;
    }

    private boolean exists(int index) {
        if (index < heap.length && heap[index] != null) {
            return true;
        }
        return false;
    }

    private boolean hasLeft(int index) {
        return exists(left(index));
    }

    private boolean hasRight(int index) {
        return exists(right(index));
    }

    private void swap(int index1, int index2) {
        T temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
    }

    public void add(T item) {
        indexMapping.put(item, size + 1);
        offer(item);
    }

    public T remove(T item) {
        if (!contains(item)) {
            return null;
        }
        T output = item;
        int itemIndex = indexMapping.remove(item);
        if (itemIndex == 1) {
            return poll();
        }
        int tempIndex = itemIndex;
        while (hasRight(tempIndex)) {
            tempIndex = right(tempIndex);
        }
        if (hasLeft(tempIndex)) {
            tempIndex = left(tempIndex);
        }
        if (itemIndex != tempIndex) {
            heap[itemIndex] = heap[tempIndex];
            heap[tempIndex] = null;
            bubbleDown(itemIndex);
        }
        // had no children; thus on the last height
        if (itemIndex != size) { // if item is not the last
            heap[itemIndex] = heap[size]; // replace item with the last item
        }
        heap[size] = null;
        size--;

        return output;
    }

    public boolean contains(T item) {
        if (indexMapping.containsKey(item)) {
            return true;
        }
        return false;
    }

    private void resize(int newCapacity) {
        T[] newHeap = (T[]) new Object[newCapacity];
        for (int i = 1; i <= size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }

    @Override
    public void offer(T item) {
        if (size + 1 == heap.length) {
            resize(heap.length * 2);
        }
        heap[size + 1] = item;
        size++;
        int itemIndex = bubbleUp(size);
        indexMapping.put(item, itemIndex);
    }

    private int bubbleUp(int curIndex) {
        if (curIndex == 1) { // I'm at the root
            return curIndex;
        }

        T parent = heap[parent(curIndex)];
        T myself = heap[curIndex];
        if (comparator.compare(myself, parent) < 0) {
            swap(curIndex, parent(curIndex));
            bubbleUp(parent(curIndex));
        }
        return curIndex;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T peek() {
        return heap[1];
    }

    @Override
    public T poll() {
        T out = peek();

        T replacement = heap[size];
        heap[size] = null;
        heap[1] = replacement;
        size--;
        bubbleDown(1);
        return out;
    }

    private void bubbleDown(int curIndex) {
        if (!hasLeft(curIndex)) {
            return;
        }

        if (!hasRight(curIndex)) {
            T myself = heap[curIndex];
            T left = heap[left(curIndex)];
            if (comparator.compare(left, myself) < 0) {
                swap(curIndex, left(curIndex));
                bubbleDown(left(curIndex));
            }
        } else {
            T myself = heap[curIndex];
            T left = heap[left(curIndex)];
            T right = heap[right(curIndex)];

            if (comparator.compare(left, right) < 0) {
                if (comparator.compare(left, myself) < 0) {
                    swap(curIndex, left(curIndex));
                    bubbleDown(left(curIndex));
                }
            } else {
                if (comparator.compare(right, myself) < 0) {
                    swap(curIndex, right(curIndex));
                    bubbleDown(right(curIndex));
                }
            }
        }
    }

    @Override
    public void updatePriority(T item) {

        int itemIndex = indexMapping.get(item);
        if (itemIndex != 1 && comparator.compare(heap[itemIndex], heap[parent(itemIndex)]) < 0) {
            bubbleUp(itemIndex);
        } else {
            bubbleDown(itemIndex);
        }
    }

}
