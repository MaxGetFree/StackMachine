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
    void letGo(LinkedList st, char oper) {

        int someOne = (Integer)st.removeLast();
        float someTwo = (Float)st.removeLast();


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
                System.out.println("Unknown operation");
        }
    }

    //Парсер и результат
    Object eval(String s) {

        // Связные списки для операций и чисел
        LinkedList digits = new LinkedList<>();
        LinkedList<Character> operators = new LinkedList<>();

        // Цикл пробегает по входной строке
        for(int i = 0; i < s.length(); i++) {

            char c = s.charAt(i);

            if(c == '(') {

                operators.add('(');

            }

            else if (c == ')') {

                while(operators.getLast() != '(') {
                    letGo(digits, operators.removeLast());
                }
                operators.removeLast();
            }

            else if (isOperator(c)) {
                while(!operators.isEmpty() &&
                        priority(operators.getLast()) >= priority(c)) {
                    letGo(digits, operators.removeLast());
                }

                operators.add(c);
            }


            else {


                String operand = "";
                boolean isFloat = false;

                while(i < s.length() &&
                        (Character.isDigit(s.charAt(i))||s.charAt(i)=='.')) {
                    if (s.charAt(i)=='.') isFloat=true;
                    operand += s.charAt(i++);

                }

                --i;

                if (isFloat) {
                    digits.add(Float.parseFloat(operand));
                }
                else {
                    digits.add(Integer.parseInt(operand));
                }

            }
        }


        while(!operators.isEmpty()) {

            letGo(digits, operators.removeLast());

        }

        return digits.get(0);
    }
}
