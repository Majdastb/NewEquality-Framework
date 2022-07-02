import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class TraverseTree {

    // Function to traverse tree without recursion
    public String TraverseTreeString (ParseTree root) {


        // Stack to store the nodes
        Stack<ParseTree> nodes = new Stack<>();
        StringBuilder save_string = new StringBuilder();

        // push the current node onto the stack
        nodes.push(root);


        // Loop while the stack is not empty
        while (!nodes.isEmpty()) {
            // Store the current node and pop
            // it from the stack
            ParseTree curr = nodes.pop();
            // Current node has been traversed
            if (curr != null) {
               // if (! (curr.getText().isEmpty())) System.out.println(curr.getText() + " ");

                if ((curr instanceof TerminalNode) && !curr.getText().isEmpty()) {
                    save_string.append(curr.getText()) ;
                    save_string.append(" ") ; }
                // Store all the children of
                // current node from right to left.
                for (int i = curr.getChildCount() - 1; i >= 0; i--) {
                    nodes.add(curr.getChild(i));
                }

            }
        }
        return String.valueOf(save_string).trim() ;
    }





    public List<String> TraverseTreeList (ParseTree root) {


        // Stack to store the nodes
        Stack<ParseTree> nodes = new Stack<>();
        StringBuilder save_string = new StringBuilder();
        List<String> label_attribute = new ArrayList<>();
        // push the current node onto the stack
        nodes.push(root);

        // Loop while the stack is not empty
        while (!nodes.isEmpty()) {
            // Store the current node and pop
            // it from the stack
            ParseTree curr = nodes.pop();

            //System.out.println(curr.getChildCount());
            // Current node has been traversed
            if (curr != null) {

                if (curr instanceof TerminalNode && !curr.getText().isEmpty() ) label_attribute.add(curr.getText());
                // Store all the childrent of
                // current node from right to left.
                for (int i = curr.getChildCount() - 1; i >= 0; i--) {
                    // System.out.print("boucle for ");
                    nodes.add(curr.getChild(i));
                }

            }
        }
        return label_attribute;
    }
}
