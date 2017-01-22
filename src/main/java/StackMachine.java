import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackMachine {

    public static void main(String args[])
    {
        System.out.println("Введите выражение:");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        StackMachine sm = new StackMachine();
        System.out.println(sm.parse(input));
    }

    //Здесь всякие функции тригонометрические можно добавить
    private final String[] FUNCTIONS = { "sin", "cos" };
    //Разделитель для входных строк
    private final String SEPARATOR = ";";

    private final String OPERATORS = "+-*/";

    //разбор операторов
    boolean isOperator(String tok) {
        return OPERATORS.contains(tok);
    }

    //разбор функций
    private boolean isFunction(String tok) {
        for (String item : FUNCTIONS) {
            if (item.equals(tok)) {
                return true;
            }
        }
        return false;
    }

    //Является ли строка математическим выражением
    boolean isExpression (String someToken)
    {
        boolean flag = false;

        //Делим строку someToken на подстроки( в качестве разделителей используются операторы и скобки)
        StringTokenizer stringTokenizer = new StringTokenizer(someToken, OPERATORS + "()", false);
        //Пробегаем по полученным подстрокам
        while (stringTokenizer.hasMoreTokens())
        {
            String token = stringTokenizer.nextToken();
            //Если полученная подстрока - функция, ставим флаг true
            if (isFunction(token)){
                flag=true;
            }
            else {
                //Если полученная подстрока - число, ставим флаг true
                try
                {
                    Double.parseDouble(token);
                    flag=true;

                }
                //Если встречаем что-то кроме функций и чисел, ставим флаг false и выходим из цикла
                catch (NumberFormatException e)
                {
                    flag=false;
                    break;
                }
            }
        }
        return flag;
    }

    //приоритет операций
    int priority(String oper) {
        if (oper.equals("+") || oper.equals("-")) {
            return 1;
        }
        else if (oper.equals("(")||oper.equals(")")) {
            return 0;
        }
        else return 2;

    }

    //вычисление операций и запись результатов в "стек"
    //достаём из стека два последних числа и выполняем над ними операцию
    void calculate(LinkedList<Double> st, String oper) {

        double firstDigit;
        double secondDigit;
        if (isFunction(oper))
        {
            firstDigit = st.removeLast();
            switch(oper) {
                case "cos":
                    st.add(Math.cos(firstDigit));
                    break;
                case "sin":
                    st.add(Math.sin(firstDigit));
                    break;
            }
        }
        else
        {
            firstDigit = st.removeLast();
            secondDigit = st.removeLast();
            switch(oper) {
                case "+":
                    st.add(secondDigit + firstDigit);
                    break;
                case "-":
                    st.add(secondDigit - firstDigit);
                    break;
                case "*":
                    st.add(secondDigit * firstDigit);
                    break;
                case "/":
                    st.add(secondDigit / firstDigit);
                    break;
            }
        }

    }



    //Парсер выражения и вычисление результата
    Double eval(String s) {

        // Связные списки для операций и чисел
        LinkedList<Double> digits = new LinkedList<>();
        LinkedList<String> operators = new LinkedList<>();

        StringTokenizer stringTokenizer = new StringTokenizer(s, OPERATORS+"()", true);
        while (stringTokenizer.hasMoreTokens())
        {
            String token = stringTokenizer.nextToken();

            if(token.equals("(")) {
                operators.add("(");
            }

            //Если встретили закрывающуюся скобку, выполняем все операции до открывающейся, удаляя их при этом из стека
            else if (token.equals(")")) {

                while(!operators.getLast().equals("(")) {
                    calculate(digits, operators.removeLast());
                }
               operators.removeLast();
            }

            //Добавляем операторы и функции в стек и вычисляем предшествующий если приоритет больше или равен
            else if (isOperator(token)||isFunction(token)) {
                while(!operators.isEmpty() &&
                        priority(operators.getLast()) >= priority(token)) {
                    calculate(digits, operators.removeLast());
                }
                operators.add(token);
            }

            //добавляем числа в стек
            else {
                digits.add(Double.parseDouble(token));
            }
        }

        //Вычисляем оставшиеся операторы
        while(!operators.isEmpty()) {
            try {
                calculate(digits, operators.removeLast());
            }
            catch (NoSuchElementException e) {
                System.out.println("Выражение записано неверно");
                e.printStackTrace();
            }
        }

        return digits.get(0);
    }


    String parse(String input)
    {
        //Список предложений, поданных на вход
        LinkedList<String> main_tokens = new LinkedList<>();
        //Карта результатов вычесленных выражений и их идентификаторов
        Map<Integer, Double> results = new HashMap<Integer,Double>();

        //Счетчик для вычисления номера выражений по порядку в исходном сообщении
        int counter = 0;
        String result="";

        //Разбиваем исходную строку на подстроки с помощью разделителя указанного в переменной SEPARATOR
        //Третий параметр (false) не включает разделители в полученные подстроки
        StringTokenizer mainTokenizer = new StringTokenizer(input, SEPARATOR, false);

        //Пробегаем в цикле по полученным подстрокам
        while (mainTokenizer.hasMoreTokens()) {
            String main_token = mainTokenizer.nextToken();
            //Если подстрока является выражением - считаем его и записываем результат в массив с индексом counter
            if (isExpression(main_token)) {
                //Каждый раз когда встречаем выражение инкрементим счетчик
                counter++;
                results.put(counter,eval(main_token));
            }
            //В противном случае просто храним подстроку
            else main_tokens.add(main_token);
        }

        //Соединяем все полученные подстроки (не выражения)
        while (!main_tokens.isEmpty())
        {
            result = result.concat(main_tokens.removeFirst());
        }

        //Замена ссылок значениями (примем, что ссылка на выражение оформляется с помощью следующей конструкции:{№выражения}
        //пробегаем циклом всю полученную строку
        for (int i = 0;i < result.length();i++) {
            char ch = result.charAt(i);
            //Если встречаем открывающуюся скобку - ищем соответствующую ей закрывающуюся
            if(ch == '{') {
                for (int j = i;j < result.length();j++) {
                    char c = result.charAt(j);
                    if(c == '}') {
                        try {
                            //Получаем номер выражения, значение которого необходимо вставить
                            int index = Integer.parseInt(result.substring(i+1,j));
                            //составляем результирующую строку со вставленным значением выражения
                            result = result.substring(0,i) + " " + results.remove(index) + " " + result.substring(j+1,result.length());
                            //если нашли закрывающуюся скобку выходим из текущего цикла
                            break;
                        }
                        catch (NumberFormatException e){
                            System.out.println("Ссылки могут задаваться только числами");
                        }
                    }
                }
            }
        }

        //Если остались выражения на которые не было ссылок в тексте, просто добавляем их в результирующую строку
        if (!results.isEmpty())
        {
            for (Map.Entry<Integer, Double> entry : results.entrySet()) {
                 result = result + " " + entry.getValue();
            }
        }
        return result;
    }


}
