package manager;

import task.Task;


import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    List<Task> listHistory = new ArrayList<>();
    Map<Integer, Node> mapHistory = new HashMap<>();
    CustomLinkedList<Task> linkedListHistory = new CustomLinkedList<>();


    @Override
    public void add(Task task) {
        if (mapHistory.containsKey(task.getId())) {
            linkedListHistory.removeNode(mapHistory.get(task.getId()));
            mapHistory.remove(task.getId());
        }
        linkedListHistory.linkLast(task);
        mapHistory.put(task.getId(), linkedListHistory.tail);
    }

    @Override
    public List<Task> getHistory() {
        if (linkedListHistory.getSize() != 0) {
            listHistory = linkedListHistory.getTasks();
            return listHistory;
        }
        return List.of();
    }

    @Override
    public void remove(int id) {
        if (mapHistory.containsKey(id)) {
            linkedListHistory.removeNode(mapHistory.get(id));
        }
    }


    static class CustomLinkedList<Task> {
        private Node<Task> head;

        private Node<Task> tail;

        private int size = 0;

        void linkLast(Task task) {

            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(task, null, oldTail);
            tail = newNode;

            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        List<Task> getTasks() {
            List<Task> newListHistory = new ArrayList<>();
            for (Node<Task> x = head; x != null; x = x.next) {
                newListHistory.add(x.data);
            }
            return newListHistory;
        }

        void removeNode(Node<Task> x) {
            final Task element = x.data;
            final Node<Task> next = x.next;
            final Node<Task> prev = x.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                x.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                x.next = null;
            }

            x.data = null;
            size--;

        }

        int getSize() {
            return size;
        }

    }
}