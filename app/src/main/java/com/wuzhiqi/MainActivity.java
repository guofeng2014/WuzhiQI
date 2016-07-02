package com.wuzhiqi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wuzhiqi.view.WUZhiQIView;

public class MainActivity extends AppCompatActivity implements WUZhiQIView.OnWinCallBack {
    private WUZhiQIView wzq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wzq = (WUZhiQIView) findViewById(R.id.view_wzp);
        wzq.setWinCallBack(this);
    }

    /**
     * 提示赢了对话框
     *
     * @param isWhiteWinner
     */
    private void showWinDialog(boolean isWhiteWinner) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("温馨提示");
        String content = isWhiteWinner ? "白方胜利" : "黑方胜利";
        dialog.setMessage(content + "\n在来一局?");
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                wzq.onRestart();
            }
        });
        dialog.show();
    }

    @Override
    public void winListener(boolean isWhiteWinner) {
        showWinDialog(isWhiteWinner);
    }
}
