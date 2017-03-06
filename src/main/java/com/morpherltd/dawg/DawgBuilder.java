package com.morpherltd.dawg;

import java.util.ArrayList;
import java.util.List;

public class DawgBuilder<TPayload> {
    final Node<TPayload> root = new Node<TPayload>();

    final ArrayList<Node<TPayload>> lastPath = new ArrayList<>();
    String lastKey = "";

    Class<TPayload> cls;

    public DawgBuilder(Class<TPayload> c) {
        cls = c;
    }

    public void insert(Iterable<Character> key, TPayload value) {
        String strKey = String.valueOf(key);
        if (strKey != null) { // TODO: same in Java?
            insertLastPath(strKey, value);
        } else {
            doInsert(root, key, value);
        }
    }

    private void insertLastPath(String strKey, TPayload value) {
        int i = 0;

        while (i < strKey.length() && i < lastKey.length()) {
            if (strKey.charAt(i) != lastKey.charAt(i)) break;
            ++i;
        }

        lastPath.subList(i, lastPath.size()).clear();

        lastKey = strKey;

        Node<TPayload> node = i == 0 ? root : lastPath.get(i - 1);

        while (i < strKey.length()) {
            node = node.getOrAddEdge(strKey.charAt(i));
            lastPath.add(node);
            ++i;
        }

        node.setPayload(value);
    }

    private static <TPayload> void doInsert(Node<TPayload> node,
                                            Iterable<Character> key,
                                            TPayload value) {
        for (char c : key) {
            node = node.getOrAddEdge(c);
        }
        node.setPayload(value);
    }

    private boolean tryGetValue(Iterable<Character> key, _<TPayload> value) {
        try {
            value.s(cls.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Node<TPayload> node = this.root;

        for (char c : key) {
            node = node.getChild(c);

            if (node == null) return false;
        }

        value.s(node.getPayload());

        return true;
    }

    public Dawg<TPayload> buildDawg() {
        LevelBuilder<TPayload>.buildLevelsExcludingRoot(root);
        return new Dawg<TPayload>(new OldDawg<TPayload>(root));
    }
}