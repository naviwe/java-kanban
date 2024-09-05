package manager;

import task.Task;


import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node> mapHistory = new HashMap<>();
    CustomLinkedList<Task> linkedListHistory = new CustomLinkedList<>();


    @Override
    public void add(Task task) {
        if (task != null) {
            if (mapHistory.containsKey(task.getId())) {
                linkedListHistory.removeNode(mapHistory.get(task.getId()));
                mapHistory.remove(task.getId());
            }
            linkedListHistory.linkLast(task);
            mapHistory.put(task.getId(), linkedListHistory.tail);
        }
    }

    @Override
    public List<Task> getHistory() {
        if (linkedListHistory.getSize() != 0) {
            return linkedListHistory.getTasks();
        }
        return List.of();
    }

    @Override
    public void remove(int id) {
        if (mapHistory.containsKey(id)) {
            linkedListHistory.removeNode(mapHistory.get(id));
            mapHistory.remove(id);
        }
    }


    static class CustomLinkedList<T extends Task> {
        private Node<T> head;

        private Node<T> tail;

        private int size = 0;

        void linkLast(T task) {

            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(task, null, oldTail);
            tail = newNode;

            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        List<Task> getTasks() {
            List<Task> newListHistory = new ArrayList<>();
            for (Node<T> x = head; x != null; x = x.next) {
                newListHistory.add(x.data);
            }
            return newListHistory;
        }

        void removeNode(Node<T> node) {
            if (node == null) {
                return;
            }
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
                if (head != null) {
                    node.prev = null;
                }
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
                if (tail != null) {
                    node.next = null;
                }
            }
        }

        int getSize() {
            return size;
        }

    }
}