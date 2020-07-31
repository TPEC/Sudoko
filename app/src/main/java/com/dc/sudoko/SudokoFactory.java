package com.dc.sudoko;

import java.util.Random;

/**
 * Created by XIeQian on 2017/2/10.
 */

public class SudokoFactory {
    private int[] d;
    private boolean[] f;
    private int l;
    private int[] sta;
    private boolean[] staf;
    private int stai;
    private int tim;

    public SudokoFactory(){
        d=new int[81];
        f=new boolean[729];
        sta=new int[81];
        staf=new boolean[729];
        tim=0;
    }

    private void price(){
        int i,j,k;
        int x,y;
        for(i=0;i<729;i++){
            f[i]=true;
        }
        for(i=0;i<81;i++){
            if(d[i]>0){
                x=i%9;
                y=i/9;
                for(j=0;j<9;j++){
                    f[(y*9+j)*9+d[i]-1]=false;
                    f[(j*9+x)*9+d[i]-1]=false;
                }
                for(j=(y/3)*3;j<(y/3+1)*3;j++)
                    for(k=(x/3)*3;k<(x/3+1)*3;k++)
                        f[(j*9+k)*9+d[i]-1]=false;
            }
        }
    }

    private void rerankl(){
        int[] c=new int[81];
        int i,j,m;
        for(i=0;i<81;i++){
            c[i]=0;
            if(d[i]==0){
                for(j=0;j<9;j++){
                    if(f[i*9+j]){
                        c[i]++;
                        m=j;
                    }
                }
            }
        }
        l=-1;
        m=10;
        for(j=0;j<81;j++){
            if(c[j]<m && d[j]==0){
                m=c[j];
                l=j;
            }
        }
    }

    private void run(){
        tim++;
        int i,j,g;
        boolean flag,tf;
        Random rnd=new Random();
        int[] lis=new int[10];
        price();
        rerankl();
        if(l==-1){
            return;
        }
        else{
            if(stai==-1 || sta[stai]!=l){
                stai++;
                sta[stai]=l;
            }
            tf=true;
            while(tf) {
                tf=false;
                flag = false;
                lis[9] = -1;
                for (j = 0; j < 9; j++) {
                    if (f[l * 9 + j] && !staf[stai * 9 + j]) {
                        lis[9]++;
                        lis[lis[9]] = j;
                        flag = true;
                    }
                }
                if (flag) {
                    if ((1 / 1) % 2 == 1)
                        j = lis[Math.abs(rnd.nextInt()) % (lis[9] + 1)];
                    else
                        j = lis[0];
                    d[l] = j + 1;
                    staf[stai * 9 + j] = true;
                    run();
                    return;
                } else {
                    d[sta[stai]] = 0;
                    for (j = 0; j < 9; j++)
                        staf[stai * 9 + j] = false;
                    stai--;
                    l = sta[stai];
                    price();
                    tf=true;
                }
            }
        }
    }

    public int[] create(){
        int i,j;
        for(i=0;i<81;i++){
            d[i]=0;
            sta[i]=-1;
            for(j=0;j<9;j++){
                f[i*9+j]=true;
                staf[i*9+j]=false;
            }
        }
        stai=-1;
        run();
        return d;
    }

    public boolean[] mask(int count){
        Random rnd=new Random();
        boolean[] m=new boolean[81];
        for(int i=0;i<81;i++)
            m[i]=false;
        while (count>0){
            int i= Math.abs(rnd.nextInt())%81;
            if(!m[i]){
                m[i]=true;
                count--;
            }
        }
        return m;
    }
}
