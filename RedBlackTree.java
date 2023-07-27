
import java.util.Iterator;

public class RedBlackTree<T extends Comparable<T>> implements Iterable<T>, Iterator<T> {

    /**
     * Перечисление цветов узла дерева.
     */
    enum NodeColor {
        RED,
        BLACK,
        NONE
    }

    /**
     * Класс реализующий узел дерева.
     */
    public class Node {

        /**
         * Значение узла дерева.
         */
        private T _value;
        /**
         * Цвет узла.
         */
        private NodeColor _color;
        /**
         * Родительский узел.
         */
        private Node _parent;
        /**
         * Левый дочерниый узел.
         */
        private Node _left;
        /**
         * Правый дочерний узел.
         */
        private Node _right;

        /**
         * Конструктор по-умолчанию.
         */
        public Node() {
            _value = null;
            _color = NodeColor.NONE;
            _parent = null;
            _left = null;
            _right = null;
        }

        /**
         * Конструктор с параметрами, позволящими задать цвет и
         * значение узла.
         *
         * @param value - значение, которое будет сохранено в узле.
         * @param color - цвет узла.
         */
        public Node(T value, NodeColor color) {
            _value = value;
            _color = color;
            _parent = _nil;
            _left = _nil;
            _right = _nil;
        }

        /**
         * Конструктор копий.
         *
         * @param node - другой узел.
         */
        public Node(Node node) {
            _value = node._value;
            _color = node._color;
            _parent = node._parent;
            _left = node._left;
            _right = node._right;
        }

        public boolean isFree() {
            return _value == null || _value == _nil;
        }

        public boolean isLeftFree() {
            return _left == null || _left == _nil;
        }

        public boolean isRightFree() {
            return _right == null || _right == _nil;
        }

        public boolean isParentFree() {
            return _parent == null || _parent == _nil;
        }

        public T getValue() {
            return _value;
        }

        public void setValue(T value) {
            _value = value;
        }

        public Node getParent() {
            return _parent;
        }

        public void setParent(Node node) {
            _parent = node;
        }

        public Node getLeft() {
            return _left;
        }

        public void setLeft(Node node) {
            _left = node;
            if (_left != null && _left != _nil) _left._parent = this;
        }

        public Node getRight() {
            return _right;
        }

        public void setRight(Node node) {
            _right = node;
            if (_right != null && _right != _nil) _right._parent = this;
        }

        public boolean isBlack() {
            return _color == NodeColor.BLACK;
        }

        public void makeBlack() {
            _color = NodeColor.BLACK;
        }

        public boolean isRed() {
            return _color == NodeColor.RED;
        }

        public void makeRed() {
            _color = NodeColor.RED;
        }

        public NodeColor getColor() {
            return _color;
        }

        public void setColor(NodeColor color) {
            _color = color;
        }

        /**
         * Возвращет "дедушку" узла дерева.
         */
        public Node getGrandfather() {
            if (_parent != null && _parent != _nil)
                return _parent._parent;
            return null;
        }

        /**
         * Возвращает "дядю" узла дерева.
         */
        public Node getUncle() {
            Node grand = getGrandfather();
            if (grand != null) {
                if (grand._left == _parent)
                    return grand._right;
                else if (grand._right == _parent)
                    return grand._left;
            }
            return null;
        }

        /**
         * Возвращает следующий по значению узел дерева.
         */
        public Node getSuccessor() {
            Node temp = null;
            Node node = this;
            if (!node.isRightFree()) {
                temp = node.getRight();
                while (!temp.isLeftFree())
                    temp = temp.getLeft();
                return temp;
            }
            temp = node.getParent();
            while (temp != _nil && node == temp.getRight()) {
                node = temp;
                temp = temp.getParent();
            }
            return temp;
        }

        public String getColorName() {
            return ((isBlack()) ? "B" : "R");
        }

    }

    /**
     * Корень дерева.
     */
    private Node _root;
    /**
     * Ограничитель, который обозначает нулевую ссылку.
     */
    private Node _nil;

    /**
     * Ссылка на элемент на который указывает итератор.
     */
    private Node _current;

    /**
     * Флаг удаления элемента через итератор, необходимый для того, чтобы
     * корректно работали {@link Iterator#hasNext()} и {@link Iterator#next()}
     */
    private boolean _isRemoved;

    /**
     * Конструктор по-умолчанию.
     */
    public RedBlackTree() {
        _root = new Node();
        _nil = new Node();
        _nil._color = NodeColor.BLACK;
        _root._parent = _nil;
        _root._right = _nil;
        _root._left = _nil;
    }

    /**
     * Статический метод, осуществляющий левый поворот дерева tree относительно узла node.
     *
     * @param tree - дерево.
     * @param node - узел, относительно которого осуществляется левый поворот.
     */
    private static <T extends Comparable<T>> void leftRotate(RedBlackTree<T> tree, RedBlackTree<T>.Node node) {
        RedBlackTree<T>.Node nodeParent = node.getParent();
        RedBlackTree<T>.Node nodeRight = node.getRight();
        if (nodeParent != tree._nil) {
            if (nodeParent.getLeft() == node)
                nodeParent.setLeft(nodeRight);
            else
                nodeParent.setRight(nodeRight);
        } else {
            tree._root = nodeRight;
            tree._root.setParent(tree._nil);
        }
        node.setRight(nodeRight.getLeft());
        nodeRight.setLeft(node);
    }

    /**
     * Статический метод, осуществляющий правый поворот дерева tree относительно узла node.
     *
     * @param tree - дерево.
     * @param node - узел, относительно которого осуществляется правый поворот.
     */
    private static <T extends Comparable<T>> void rightRotate(RedBlackTree<T> tree, RedBlackTree<T>.Node node) {
        RedBlackTree<T>.Node nodeParent = node.getParent();
        RedBlackTree<T>.Node nodeLeft = node.getLeft();
        if (nodeParent != tree._nil) {
            if (nodeParent.getLeft() == node)
                nodeParent.setLeft(nodeLeft);
            else
                nodeParent.setRight(nodeLeft);
        } else {
            tree._root = nodeLeft;
            tree._root.setParent(tree._nil);
        }
        node.setLeft(nodeLeft.getRight());
        nodeLeft.setRight(node);
    }

    /**
     * Реализация метода добавления элемента дерева. На основе добавляемого значения
     * создается узел дерева типа {@link Node} красного цвета.
     *
     * @param o - значение типа {@link Comparable} для вставки в дерево.
     */
    public void add(T o) {
        Node node = _root, temp = _nil;
        Node newNode = new Node((T) o, NodeColor.RED);
        while (node != null && node != _nil && !node.isFree()) {
            temp = node;
            if (newNode.getValue().compareTo(node.getValue()) < 0)
                node = node.getLeft();
            else
                node = node.getRight();
        }
        newNode.setParent(temp);
        if (temp == _nil)
            _root.setValue(newNode.getValue());
        else {
            if (newNode.getValue().compareTo(temp.getValue()) < 0)
                temp.setLeft(newNode);
            else
                temp.setRight(newNode);
        }
        newNode.setLeft(_nil);
        newNode.setRight(_nil);
        fixInsert(newNode);
    }

    /**
     * Исправление древа для сохранения свойств красно-черного дерева.
     *
     * @param node - добавленный узел.
     */
    private void fixInsert(Node node) {
        Node temp;
        while (!node.isParentFree() && node.getParent().isRed()) {
            if (node.getParent() == node.getGrandfather().getLeft()) {
                temp = node.getGrandfather().getRight();
                if (temp.isRed()) {
                    temp.makeBlack();
                    node.getParent().makeBlack();
                    node.getGrandfather().makeRed();
                    node = node.getGrandfather();
                } else {
                    if (node == node.getParent().getRight()) {
                        node = node.getParent();
                        leftRotate(this, node);
                    }
                    node.getParent().makeBlack();
                    node.getGrandfather().makeRed();
                    rightRotate(this, node.getGrandfather());
                }
            } else {
                temp = node.getGrandfather().getLeft();
                if (temp.isRed()) {
                    temp.makeBlack();
                    node.getParent().makeBlack();
                    node.getGrandfather().makeRed();
                    node = node.getGrandfather();
                } else {
                    if (node == node.getParent().getLeft()) {
                        node = node.getParent();
                        rightRotate(this, node);
                    }
                    node.getParent().makeBlack();
                    node.getGrandfather().makeRed();
                    leftRotate(this, node.getGrandfather());
                }
            }
        }
        _root.makeBlack();
    }
}

