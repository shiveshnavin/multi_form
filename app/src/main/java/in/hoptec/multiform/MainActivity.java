package in.hoptec.multiform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String TAG="SS";
    public static String URL="https://test-a0930.firebaseio.com/multiform";

    boolean isReady=false;
    public int cur_step=0;
    ArrayList<MyAnswer> answers;

    public CheckBox getCheckBox(String name)
    {

        CheckBox ck=new CheckBox(this);
        ck.setChecked(false);
        ck.setText(name);

        return ck;
    }

    TextView ques;
    LinearLayout options;
    Button next;


    Firebase fb;
    DataSnapshot ds;
    DataSnapshot cursor;
    DataSnapshot pcursor;
    ProgressBar prog;

    int maxCount=6;
    int curCount=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ques=(TextView)findViewById(R.id.ques);
        options=(LinearLayout)findViewById(R.id.options);
        next=(Button)findViewById(R.id.next);
        prog=(ProgressBar)findViewById(R.id.prog);

        maxCount=getIntent().getIntExtra("question_count",6);


        Firebase.setAndroidContext(this);
        fb=new Firebase(URL);
        answers=new ArrayList<>();

        fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(ds==null)
                {
                    ds=dataSnapshot;
                    inflate(ds.child("ques0"));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isReady)
                {
                    Toast.makeText(MainActivity.this,"Select An Option First !",Toast.LENGTH_LONG).show();
                    return;
                }
                double mx=maxCount;
                double cx=curCount++;

                double pr=100*(cx/mx) ;
                Log.d(TAG, "onClick: prog"+pr+" cur  "+curCount+" max "+maxCount);

                if(pr>90)
                {
                    //// TODO: Submit the form
                    Toast.makeText(MainActivity.this,"Form Complete !",Toast.LENGTH_LONG).show();
                    return;

                }
                prog.setProgress((int)pr);
                if(cursor!=null)
                {

                    MyAnswer ans=new MyAnswer();

                    final int childCount = options.getChildCount();
                    for (int i = 0; i < childCount; i++) {

                        CheckBox v =(CheckBox) options.getChildAt(i);

                        if(v.isChecked())
                        ans.answer+=v.getText().toString()+" | ";


                    }

                    ans.question=ques.getText().toString();
                    ans.path=pcursor.getRef().getPath().toString();
                    answers.add(ans);

                    int i=0;
                    for(MyAnswer answer:answers)
                    {
                        Log.d(TAG, "My Answer No. : "+i+" \nQ :  "+answer.question+" \nAns(s) : "+answer.answer);
                        i++;
                    }

                    inflate(cursor);
                    isReady=false;
                }
            }
        });

    }


    Option curo;
    public void inflate(DataSnapshot cr)
    {

       // Log.d(TAG, "inflate:  "+cr.getValue().toString());

        pcursor=cr;
        final String question=cr.child("name").getValue().toString();
        final String option_type=cr.child("option_type").getValue().toString();
        ArrayList<Option> opts=new ArrayList<>();
        int i=0;



       // Log.d(TAG, "inflate: ques "+question);

        ques.setText(question);

        for (DataSnapshot dsa:cr.child("options").getChildren())
        {

            Option op=new Option();
            op.name=dsa.child("name").getValue().toString();
            op.cr=ds.child(dsa.child("next").getValue().toString());

            opts.add(op);
          //  Log.d(TAG, "inflate: option "+i +"->"+op.name+" next "+op.cr.getKey());
            i++;

        }

        options.removeAllViews();;

        for (final Option opt:opts)
        {
           final CheckBox ck=getCheckBox(opt.name);
            ck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isReady=true;
                     final int childCount = options.getChildCount();
                    for (int i = 0; i < childCount; i++) {

                        CheckBox v =(CheckBox) options.getChildAt(i);
                        if(option_type.equals("single_check"))
                            v.setChecked(false);


                        ck.setChecked(true);
                        cursor=opt.cr;
                        curo=opt;


                    }
                }
            });

            options.addView(ck);
        }





    }










}
