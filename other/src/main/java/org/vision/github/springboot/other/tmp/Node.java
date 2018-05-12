package org.vision.github.springboot.other.tmp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/** @author ganminghui */

@Data @NoArgsConstructor public class Node {
    private int id;
    private Node next;

    public Node setNext(Node node){
        this.next = node;
        return node;
    }

    public Node(int id){setId(id);}

    public static Node linkedNode(Node node){
        Objects.requireNonNull(node,"root node must not null");
        Node rootX = null,tailX = null, rootXX = null;
        Node currX = null, currXX = null;

        boolean flag = true;
        while (Objects.nonNull(node)){
            if(flag){
                if(Objects.isNull(rootX)){ rootX = node; }

                currX = Objects.isNull(currX) ? rootX : currX.setNext(node);

                if(Objects.isNull(node.getNext().getNext())){ tailX = currX;}
            }else {
                if(Objects.isNull(rootXX)){ rootXX = node; }

                currXX = Objects.isNull(currXX) ? rootXX : currXX.setNext(node);
            }
            flag = flag ? false : true;
            node = node.next;
        }
        tailX.setNext(rootXX);
        return rootX;
    }

    public static void main(String[] args) {
        Node root = new Node(1);
        root.setNext(new Node(2)).setNext(new Node(3)).setNext(new Node(4)).setNext(new Node(5)).setNext(new Node(6)).setNext(new Node(7)).setNext(new Node(8));
        System.out.println(linkedNode(root));
    }
}