import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by franc on 09.12.2016.
 */
public class StackMachine {

    public static void main(String args[])
    {
        System.out.println("Введите выражение:");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        StackMachine sm = new StackMachine();
        System.out.println(sm.eval(input));
    }

    //разбор операторов
    boolean isOperator(char c)
    {
        return c=='+'||c=='-'||c=='*'||c=='/';
    }

    //приоритет операций
    int priority(char oper) {

        if(oper == '*' || oper == '/') {
            return 1;
        }

        else if(oper == '+' || oper == '-') {
            return 0;
        }

        else {
            return -1;
        }
    }

    //вычисление операций и запись результатов в "стек"
    void letGo(LinkedList<Integer> st, char oper) {


        int someOne = st.removeLast();
        int someTwo = st.removeLast();


        switch(oper) {
            case '+':
                st.add(someTwo + someOne);
                break;
            case '-':
                st.add(someTwo - someOne);
                break;
            case '*':
                st.add(someTwo * someOne);
                break;
            case '/':
                st.add(someTwo / someOne);
                break;
            default:
                System.out.println("Oops");
        }
    }

    //Парсер и результат
    int eval(String s) {

        // Связные списки для операций и чисел
        LinkedList<Integer> someInts = new LinkedList<>();
        LinkedList<Character> someOpers = new LinkedList<>();

        // Цикл пробегает по входной строке
        for(int i = 0; i < s.length(); i++) {

            char c = s.charAt(i);

            if(c == '(') {

                someOpers.add('(');

            }

            else if (c == ')') {

                while(someOpers.getLast() != '(') {
                    letGo(someInts, someOpers.removeLast());
                }
                someOpers.removeLast();
            }

            else if (isOperator(c)) {
                while(!someOpers.isEmpty() &&
                        priority(someOpers.getLast()) >= priority(c)) {
                    letGo(someInts, someOpers.removeLast());
                }

                someOpers.add(c);
            }


            else {


                String operand = "";


                while(i < s.length() &&
                        Character.isDigit(s.charAt(i))) {

                    operand += s.charAt(i++);

                }

                --i;
                someInts.add(Integer.parseInt(operand));

            }
        }


        while(!someOpers.isEmpty()) {

            letGo(someInts, someOpers.removeLast());

        }

        return someInts.get(0);
    }
}
