package com.pexip.android.wrapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        Button defaultB = findViewById(R.id.defaultButton);
        defaultB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PexipOnly.class);
                startActivity(intent);
            }
        });

        Button skypeForBusiness = findViewById(R.id.sfbButton);
        skypeForBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PexipCustomizedView.class);
                intent.putExtra("node","nodeName");
                intent.putExtra("conference",getSFBConference("https://meet.lync.com/yourUrlHere", "pexipDomain"));
                intent.putExtra("userName","userName");
                intent.putExtra("bw",576);
                intent.putExtra("domain","pexipDomain");
                startActivity(intent);
            }
        });

        Button pexip = findViewById(R.id.pexipButton);
        pexip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PexipCustomizedView.class);
                intent.putExtra("node","nodeName");
                intent.putExtra("conference","conferenceId");
                intent.putExtra("userName","userName");
                intent.putExtra("bw",576);
                intent.putExtra("domain","pexipDomain");
                startActivity(intent);
            }
        });

    }

    private String getSFBConference(String link, String domain)
    {
        String[] resultArray = link.split("/");
        if(resultArray.length >= 3)
        {
            String focusId = resultArray[resultArray.length - 1];
            String userName = resultArray[resultArray.length - 2];
            String hostName = resultArray[resultArray.length - 3];

            return focusId+"-"+userName+"-"+hostName+"@"+domain;

        }

        return "";
    }

}
