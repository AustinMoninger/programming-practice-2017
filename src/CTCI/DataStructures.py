class LinkedNode:
    def __init__(self, val, next=None):
        self.val = val
        self.next = next

    def get_val(self):
        return self.val

    def set_val(self, val):
        self.val = val

    def get_next(self):
        return self.next

    def set_next(self, next):
        self.next = next

    def __str__(self):
        return str(self.val)


class LinkedList:
    def __init__(self, head=None):
        self.head = head

    def get_head(self):
        return self.head

    def set_head(self, new_head):
        self.head = new_head

    def insert_at_head(self, val):
        if not self.head:
            self.head = LinkedNode(val)
            return

        new_node = LinkedNode(val)
        new_node.set_next(head)
        head = new_node

    def insert_at_tail(self, val):
        if not self.head:
            self.head = LinkedNode(val)
            return

        node = self.head
        while(node.get_next() != None):
            node = node.get_next()
        node.next = LinkedNode(val)

    def size(self):
        size = 0
        node = self.head
        while node:
            size += 1
            node = node.get_next()
        return size

    def search(self, val):
        node = self.head
        while node:
            if node.get_val() == val:
                return True
            node = node.get_next()
        return False

    def delete_value(self, val):
        if self.head.get_val() == val:
            self.head = self.head.get_next()
            return
        node = self.head
        while node:
            if node.get_next().get_val() == val:
                if not node.get_next().get_next():
                    node.set_next(None)
                else:
                    node.set_next(node.get_next().get_next())
                return
            node = node.get_next()

    def __str__(self):
        result = "["
        node = self.head
        if node:
            result += str(node)
            node = node.get_next()
            while node:
                result += ", " + str(node)
                node = node.get_next()
        result += "]"
        return result



class TreeNode:
    def __init__(self, data, left=None, right=None):
        self.data = data
        self.left = left
        self.right = right

    def preorder(self):
        print self.data
        if self.left:
            self.left.preorder()
        if self.right:
            self.right.preorder()

    def inorder(self):
        if self.left:
            self.left.inorder()
        print self.data
        if self.right:
            self.right.inorder()

    def postorder(self):
        if self.left:
            self.left.postorder()
        if self.right:
            self.right.postorder()
        print self.data





tree = TreeNode(1)
tree.left = TreeNode(2)
tree.right = TreeNode(3)
tree.left.left = TreeNode(4)
tree.left.right = TreeNode(5)
tree.right.left = TreeNode(6)
tree.right.right = TreeNode(7)

tree.postorder()