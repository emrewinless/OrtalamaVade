package com.emre.ortalamaVade;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    final int ROWS = 30;
    LinearLayout table;
    TextView total, avgDays, avgDate, count;
    EditText[][] cells = new EditText[ROWS][3];
    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    TextView tv(String s, int size, boolean bold){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.rgb(17,17,17)); t.setGravity(Gravity.CENTER_VERTICAL); if(bold)t.setTypeface(null,1); return t; }

    @Override public void onCreate(Bundle b){ super.onCreate(b); df.setLenient(false); build(); }

    void build(){
        ScrollView outer=new ScrollView(this); outer.setFillViewport(true);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(10),dp(8),dp(10),dp(12));
        outer.addView(root);
        TextView title=tv("Ağırlıklı Ortalama Vade Hesaplama",20,true); title.setPadding(0,0,0,dp(6)); root.addView(title);
        TextView info=tv("Her satıra referans tarihi, ödeme tarihi ve tutarı girin. Vade günü ile ağırlıklı ortalamalar otomatik hesaplanır.",13,false); info.setPadding(0,0,0,dp(10)); root.addView(info);

        HorizontalScrollView hs=new HorizontalScrollView(this); table=new LinearLayout(this); table.setOrientation(LinearLayout.VERTICAL); hs.addView(table); root.addView(hs,new LinearLayout.LayoutParams(-1,dp(470)));
        addHeader();
        for(int i=0;i<ROWS;i++) addRow(i);

        TextView st=tv("ÖZET",15,true); st.setBackgroundColor(Color.LTGRAY); st.setPadding(dp(8),dp(8),dp(8),dp(8)); root.addView(st);
        total=summaryRow(root,"Toplam Tutar"); avgDays=summaryRow(root,"Ağırlıklı Ortalama Vade (Gün)"); avgDate=summaryRow(root,"Ağırlıklı Ortalama Vade Tarihi"); count=summaryRow(root,"Dolu Ödeme Sayısı");
        setContentView(outer); calculate();
    }

    TextView cell(String s, boolean bold){ TextView t=tv(s,14,bold); t.setPadding(dp(6),0,dp(6),0); t.setBackgroundColor(Color.rgb(231,230,230)); return t; }
    void addHeader(){ LinearLayout r=row(); r.addView(cell("Referans Tarihi",true),w(135)); r.addView(cell("Ödeme Tarihi",true),w(135)); r.addView(cell("Tutar",true),w(110)); r.addView(cell("Vade (Gün)",true),w(105)); table.addView(r); }
    LinearLayout row(){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); return r; }
    LinearLayout.LayoutParams w(int d){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(d),dp(42)); p.setMargins(1,1,1,1); return p; }

    void addRow(int i){
        LinearLayout r=row();
        cells[i][0]=edit("", false); cells[i][1]=edit("", false); cells[i][2]=edit("", true);
        TextView vd=cell("",false);
        r.addView(cells[i][0],w(135)); r.addView(cells[i][1],w(135)); r.addView(cells[i][2],w(110)); r.addView(vd,w(105));
        TextWatcher tw=new TextWatcher(){ public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){ updateRow(i,vd); calculate(); } public void afterTextChanged(Editable e){} };
        cells[i][0].addTextChangedListener(tw); cells[i][1].addTextChangedListener(tw); cells[i][2].addTextChangedListener(tw);
        table.addView(r);
    }
    EditText edit(String s, boolean amount){ EditText e=new EditText(this); e.setText(s); e.setTextSize(14); e.setTextColor(Color.rgb(17,17,17)); e.setSingleLine(true); e.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT); e.setPadding(dp(6),0,dp(6),0); e.setBackgroundColor(Color.WHITE); e.setInputType(amount?InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL:InputType.TYPE_CLASS_TEXT); return e; }

    void updateRow(int i, TextView out){ Date a=parse(cells[i][0].getText().toString()), b=parse(cells[i][1].getText().toString()); if(a!=null&&b!=null){ long days=(b.getTime()-a.getTime())/86400000L; out.setText(String.valueOf(days)); } else out.setText(""); }
    Date parse(String s){ if(s==null||s.trim().isEmpty())return null; try{return df.parse(s.trim());}catch(Exception e){return null;} }
    double amount(String s){ try{return Double.parseDouble(s.replace(".","").replace(",","."));}catch(Exception e){return 0;} }
    void calculate(){
        double sum=0, weightedDays=0, weightedMillis=0; int n=0;
        for(int i=0;i<ROWS;i++){
            Date pay=parse(cells[i][1].getText().toString()); double a=amount(cells[i][2].getText().toString()); Date ref=parse(cells[i][0].getText().toString());
            if(pay!=null){ n++; if(a!=0){ sum+=a; weightedMillis+=a*pay.getTime(); if(ref!=null) weightedDays+=a*((pay.getTime()-ref.getTime())/86400000.0); } }
        }
        total.setText(sum==0?"":fmt(sum)+" TL"); avgDays.setText(sum==0?"":String.format(Locale.US,"%.2f gün",weightedDays/sum));
        if(sum!=0){ Calendar c=Calendar.getInstance(); c.setTimeInMillis(Math.round(weightedMillis/sum)); avgDate.setText(df.format(c.getTime())); } else avgDate.setText(""); count.setText(String.valueOf(n));
    }
    String fmt(double x){ return String.format(Locale.US,"%,.2f",x).replace(",","X").replace(".",",").replace("X","."); }
    TextView summaryRow(LinearLayout root,String label){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); TextView l=cell(label,true); TextView v=cell("",false); r.addView(l,new LinearLayout.LayoutParams(dp(250),dp(44))); r.addView(v,new LinearLayout.LayoutParams(dp(180),dp(44))); root.addView(r); return v; }
}
