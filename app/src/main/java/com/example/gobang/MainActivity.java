package com.example.gobang;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private GobangView mGobangView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gobang,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.again_item:
                mGobangView.start();
                break;
        }
        return true;
    }

    private void initViews() {
        mGobangView= (GobangView) findViewById(R.id.wuziqi);

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("GAME_OVER！");
        builder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.setPositiveButton("Once again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGobangView.start();
            }
        });

        mGobangView.setOnGameListener(new GobangView.onGameListener() {
            @Override
            public void onGameOver(int i) {
                String str = "";
                if(GobangView.WHITE_WIN==i){
                    str = "白方胜利！";
                }else if(i==GobangView.BLACK_WIN){
                    str = "黑方胜利！";
                }else if(i==GobangView.NO_WIN){
                    str="争锋相对，和棋!";
                }
                builder.setMessage(str);
                builder.setCancelable(false);
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });
    }


}
