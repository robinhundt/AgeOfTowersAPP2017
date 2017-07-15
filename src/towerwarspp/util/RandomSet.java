package towerwarspp.util;

import java.util.*;

/**
 * Created by robin on 15.07.17.
 */
public class RandomSet<E> extends AbstractSet<E> implements RandomAccess, Iterable<E> {

    private ArrayList<E> list;
    private HashMap<E, Integer> map;

    public RandomSet() {
        list = new ArrayList<E>();
        map = new HashMap<E, Integer>();
    }

    public RandomSet(Collection<? extends E> c) {
        list = new ArrayList<E>(c.size());
        map = new HashMap<E, Integer>(c.size());
        for(E element : c) {
            map.put(element, list.size());
            list.add(element);
        }
    }

    public RandomSet(int initialCapacity) {
        list = new ArrayList<E>(initialCapacity);
        map = new HashMap<E, Integer>(initialCapacity);
    }

    public E removeAt(int index) {
        if(index >= list.size())
            return null;

        E res = list.get(index);
        map.remove(res);
        E last = list.remove(list.size() - 1);

        /* If last element in list has been remove, hole does not need filling. Otherwise replace hole with last element
        * of list */
        if(index < list.size()) {
            map.put(last, index);
            list.set(index, last);
        }
        return res;
    }

    public E get(int index) {
        return list.get(index);
    }

    public E pollRandom(Random random) {
        if(list.isEmpty()) {
            return null;
        } else {
            return removeAt(random.nextInt(list.size()));
        }
    }

    public E getRandom(Random random) {
        if(list.isEmpty()) {
            return null;
        } else {
            return get(random.nextInt(list.size()));
        }
    }

    @Override
    public boolean add(E item) {
        if(map.containsKey(item)) {
            return false;
        } else {
            map.put(item, list.size());
            list.add(item);
            return true;
        }
    }

    @Override
    public boolean remove(Object o) {
        Integer index = map.get(o);
        if(index == null) {
            return false;
        } else {
            removeAt(index);
            return true;
        }

    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Iterator<E> it = list.iterator();
            int index = -1;
            boolean removable = false;
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                index++;
                removable = true;
                return it.next();
            }

            @Override
            public void remove() {
                if(removable) {
                    removeAt(index);
                    removable = false;
                } else {
                    throw new IllegalStateException("remove can only be called after next().");
                }
            }
        };
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public int size() {
        return list.size();
    }
}
