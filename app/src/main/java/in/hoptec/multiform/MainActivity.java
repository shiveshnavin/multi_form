package in.hoptec.multiform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String TAG="SS";
    public static String URL="https://test-a0930.firebaseio.com/multiform";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ques=(TextView)findViewById(R.id.ques);
        options=(LinearLayout)findViewById(R.id.options);
        next=(Button)findViewById(R.id.next);

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
                if(cursor!=null)
                {

                    MyAnswer ans=new MyAnswer();
                    ans.answer=curo.name;
                    ans.question=ques.getText().toString();
                    ans.path=pcursor.getRef().getPath().toString();
                    answers.add(ans);

                    for(MyAnswer answer:answers)
                    {
                        Log.d(TAG, "onClick: QUES--- "+answer.question+" \nQUESTION--- "+answer.answer+"  \nPATH---  "+answer.path);
                    }

                    inflate(cursor);
                }
            }
        });

    }


    Option curo;
    public void inflate(DataSnapshot cr)
    {

        Log.d(TAG, "inflate:  "+cr.getValue().toString());

        pcursor=cr;
        String question=cr.child("name").getValue().toString();
        ArrayList<Option> opts=new ArrayList<>();
        int i=0;



        Log.d(TAG, "inflate: ques "+question);

        ques.setText(question);

        for (DataSnapshot dsa:cr.child("options").getChildren())
        {

            Option op=new Option();
            op.name=dsa.child("name").getValue().toString();
            op.cr=ds.child(dsa.child("next").getValue().toString());

            opts.add(op);
            Log.d(TAG, "inflate: option "+i +"->"+op.name+" next "+op.cr.getKey());
            i++;

        }

        options.removeAllViews();;

        for (final Option opt:opts)
        {
           final CheckBox ck=getCheckBox(opt.name);
            ck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     final int childCount = options.getChildCount();
                    for (int i = 0; i < childCount; i++) {

                        CheckBox v =(CheckBox) options.getChildAt(i);
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
