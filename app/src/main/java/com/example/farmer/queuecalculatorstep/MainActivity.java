package com.example.farmer.queuecalculatorstep;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    Queue<String> firstQueue = new LinkedList();
    Queue<String> secondQueue = new LinkedList();

    TextView tv_queue1;
    TextView tv_queue2;
    TextView tv_pop1;
    TextView tv_pop2;
    TextView tv_onecalulator;
    TextView tv_result;

    int stepIdx = -1;
    int seq = 0;
    String sResult;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //public member 생성
        tv_queue1 = (TextView)findViewById(R.id.tv_queue1);
        tv_queue2 = (TextView)findViewById(R.id.tv_queue2);
        tv_pop1 = (TextView)findViewById(R.id.tv_pop1);
        tv_pop2 = (TextView)findViewById(R.id.tv_pop2);
        tv_onecalulator = (TextView)findViewById(R.id.tv_onecalulator);
        tv_result = (TextView)findViewById(R.id.tv_result);

        //Button Listener 연동
        Button btnStep  = (Button)findViewById(R.id.btnStep);
        Button btnClear = (Button)findViewById(R.id.btnClear);
        btnStep.setOnClickListener(calOnclickListener);
        btnClear.setOnClickListener(clearOnclickListener);
    }

    Button.OnClickListener calOnclickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            stepIdx++;
            seq = 0;
            calculatorMain();

            //tv_result.setText(sResult);
        }
    };

    //------------------------------------------------------------------------
    // 사칙연사 시작
    //------------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void calculatorMain(){
        firstQueue.clear();
        secondQueue.clear();

        //1. 산술식을 queue에 넣기
        stringParsing();
        tv_queue1.setText(firstQueue.toString());
        if (stepIdx==0) return;  //step 1

        //2. Queue에 저장 된 값 중  곱셈. 나눗셈 계산
        multiplyDivideCal();

        //3 Queue에 저장 된 값 중  덧셈. 뺄샘 계산
        sResult = addSubtractCal();
        //System.out.println("괄호 최종결과 => " + sResult);
    }

    //------------------------------------------------------------------------
    // 1차. 문자열 Parsing
    //------------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Queue stringParsing(){
        String sNumList = ((EditText)findViewById(R.id.et_arithmetic)).getText().toString();
        String sOneNum = "";  //하나의 숫자열
        for(int i=0; i<sNumList.length(); i++){
            String oneChar = sNumList.substring(i, i+1);
            if ("+-*/()".indexOf(oneChar) >= 0 ){
                if (!"".equals(sOneNum)) firstQueue.offer(sOneNum);   // 3+(3  과 같이   4칙연산자와 괄호가 동시에 존재 하는 경우 때문
                firstQueue.offer(oneChar);
                sOneNum = "";
            }else{
                sOneNum += oneChar;

                if (i+1 == sNumList.length()){
                    firstQueue.offer(sOneNum);
                }
            }
        }
        return firstQueue;
    }

    //------------------------------------------------------------------------
    // 곱셈, 나눗셈 계산
    //------------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Queue multiplyDivideCal(){
        String oneChar;
        String sNum1 = "", sNum2 = "", sResult = "";
        String operator = "";
        BigDecimal nResult = new BigDecimal("0");
        while(firstQueue.peek() != null){
            oneChar  = firstQueue.poll().toString();
            seq++;
            if (stepIdx == seq) tv_queue1.setText(firstQueue.toString());  //step
            if (stepIdx == seq) tv_pop1.setText(oneChar);  //step
            if (stepIdx == seq) tv_pop2.setText("");

            if ("+-".indexOf(oneChar) >= 0 ){
                secondQueue.offer(sNum1);
                secondQueue.offer(oneChar);
                sNum1 = "";
            }else if ("*/".indexOf(oneChar) >= 0 ) {
                operator = oneChar;
            }else{
                if ("".equals(sNum1)){
                    sNum1 = oneChar;
                }else if ("".equals(sNum2)) {
                    sNum2 = oneChar;
                    if ("*".equals(operator)) {
                        nResult = (new BigDecimal(sNum1)).multiply(new BigDecimal(sNum2));
                    }else if ("/".equals(operator)) {
                        nResult = (new BigDecimal(sNum1)).divide(new BigDecimal(sNum2), 0, BigDecimal.ROUND_UP);
                    }

                    if (stepIdx == seq) tv_pop2.setText(sNum1 + " " + operator + " " + sNum2);
                    sResult  = String.valueOf(nResult);

                    sNum1    = sResult;
                    sNum2    = "";
                    operator = "";
                }
            }

            if (firstQueue.peek() == null) secondQueue.offer(sNum1);

            if (stepIdx == seq) tv_queue2.setText(secondQueue.toString());  //step
        }
        return secondQueue;
    }

    //------------------------------------------------------------------------
    // 덧셈. 뺄샘 계산
    //------------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes" })
    public String addSubtractCal(){
        String operator = "";
        String oneChar  = "";
        //if (secondQueue.peek() == null) return "";
        //System.out.println("secondQueue.poll().toString()=" + secondQueue.toString());
        BigDecimal nResult  = new BigDecimal(secondQueue.poll().toString());
        seq++;
        if (stepIdx == seq) tv_queue2.setText(secondQueue.toString());  //step
        if (stepIdx == seq) tv_pop1.setText(nResult.toString());  //step
        if (stepIdx == seq) tv_pop2.setText("");

        while(secondQueue.peek() != null){
            oneChar  = secondQueue.poll().toString();
            seq++;
            if (stepIdx == seq) tv_queue2.setText(secondQueue.toString());  //step
            if (stepIdx == seq) tv_pop1.setText(oneChar);  //step
            if (stepIdx == seq) tv_pop2.setText("");

            if ("+-".indexOf(oneChar) >= 0 ){
                operator = oneChar;
            }else{
                if (stepIdx == seq) tv_pop2.setText(nResult + " " + operator + " " + oneChar);
                if ("+".equals(operator)){
                    nResult = nResult.add(new BigDecimal(oneChar));
                }else if ("-".equals(operator)){
                    nResult = nResult.subtract(new BigDecimal(oneChar));
                }
                operator = "";
            }
            //System.out.println("oneChar=" + oneChar + " :: operator=" + operator + " :: nResult=" + nResult);
        }
        return nResult.toString();
    }

    Button.OnClickListener clearOnclickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            stepIdx = -1;
            seq = 0;
            tv_queue1.setText("");
            tv_queue2.setText("");
            tv_pop1.setText("");
            tv_pop2.setText("");
            tv_onecalulator.setText("");
            tv_result.setText("");

            //Toast toast = Toast(getApplicationContext());
            //toast.setText(sResult);
            //toast.setDuration(Toast.LENGTH_LONG);

            Toast toast = Toast.makeText(getApplicationContext(), sResult, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
            View viewToast = toast.getView();

            //Toast BackgroundColor 변경
            //color표기시 xml에서는 "#ffffff"로 하지만
            //java코드에서는 아래와 같이 rgb로 변환하여 사용해줘야 한다.
            viewToast.setBackgroundColor(Color.rgb(255,0,255));

            //Toast Font Color변경
            TextView tvToast = (TextView)viewToast.findViewById(android.R.id.message);
            tvToast.setTextColor(Color.RED);

            toast.show();

            //Toast.makeText(getApplicationContext(), sResult, Toast.LENGTH_LONG).show();
            tv_result.setText(sResult);
        }
    };
}
