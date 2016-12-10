import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;


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
    //достаём из стека два последних числа и выполняем над ними операцию
    void letGo(LinkedList st, char oper) {


        float firstDigit;
        float secondDigit;
        
        if (st.getLast().getClass()==Integer.class)
        {
            firstDigit = (Integer)st.removeLast();
        }
        else
        {
            firstDigit = (Float)st.removeLast();
        }

        if (st.getLast().getClass()==Integer.class)
        {
            secondDigit = (Integer)st.removeLast();
        }
        else
        {
            secondDigit = (Float)st.removeLast();
        }

        switch(oper) {
            case '+':
                st.add(secondDigit + firstDigit);
                break;
            case '-':
                st.add(secondDigit - firstDigit);
                break;
            case '*':
                st.add(secondDigit * firstDigit);
                break;
            case '/':
                st.add(secondDigit / firstDigit);
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
            //Если встретили закрывающуюся скобку, выполняем все операции, удаляя их при этом из стека
            //Пока не встрем соответствующую открывающуюся скобку
            else if (c == ')') {

                while(operators.getLast() != '(') {
                    letGo(digits, operators.removeLast());
                }
                operators.removeLast();
            }

            /*Добавляем операторы в стек ( если приоритет последнего оператора в стеке больше приоритета
            текущего оператора, то необходимо выполнить операцию)
             */
            else if (isOperator(c)) {
                while(!operators.isEmpty() &&
                        priority(operators.getLast()) >= priority(c)) {
                    letGo(digits, operators.removeLast());
                }

                operators.add(c);
            }

            /*добавляем числа в стек, цикл для двузначных и более чисел
            несколько чисел идущих подряд записываются в строку operand, затем преобразуем в число
             */
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

        //выполняем оставшиеся в стеке операции
        while(!operators.isEmpty()) {

            letGo(digits, operators.removeLast());

        }

        return digits.get(0);
    }
}
