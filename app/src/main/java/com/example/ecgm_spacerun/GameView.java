package com.example.ecgm_spacerun;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.support.v4.content.ContextCompat.startActivity;

//Classe onde ocorre o jogo em sim

public class GameView extends View {

    //Dimensoes da Tela
    private int canvasWidth;
    private int canvasHeigth;

    //Efeitos Sonoros
    private SoundPool soundPool;
    private int hitsound,pointsound,levelsound;

    //Jogador
    private Bitmap player;
    private int pX=60;
    private int pY;
    private int pspeed;

    //Imagem de Fundo
    private Bitmap bgImage;

    //Inimigos
    private Bitmap enemys[] = new Bitmap[5];
    private List<Enemy> EConteiner= new ArrayList<>();
    private int conteiner_count =0;
    private float espeed=10;

    //ECGM's a recolher
    private Bitmap ecgm ;
    private int ecgmx,ecgmy;
    private int ecgmspeed=12;
    private int waitcount=0;

    private boolean waitbool =false;

    //Pontuação e nivel
    private Paint scorePaint= new Paint();
    private int score;
    private Paint levelPaint= new Paint();
    private int level =1;

    //Vida
    private Bitmap life[]= new Bitmap[2];
    private int life_count=0;

    //Deteção de Proximidade
    public boolean touch_flg=false;

    private int aux=1;

    public GameView(Context context, int i) {
        super(context);

        //Iniciar os efeitos sonoros
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        hitsound= soundPool.load(context,R.raw.hit,1);
        pointsound = soundPool.load(context,R.raw.point,1);
        levelsound = soundPool.load(context,R.raw.level,1);

        SelectImage(i);
        // Escolhers os graficos dependo do valor da actividade Start_Menu


        //Colocar a vida e os ECGM's
        life[0]= BitmapFactory.decodeResource(getResources(),R.drawable.heart);
        life[1]= BitmapFactory.decodeResource(getResources(),R.drawable.heart_g);
        ecgm=BitmapFactory.decodeResource(getResources(),R.drawable.ecgm);

        //Escolher cor,tamanho, tipo de letra dos pontos e do nivel
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(50);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);

        levelPaint.setColor(Color.WHITE);
        levelPaint.setTextSize(50);
        levelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        levelPaint.setAntiAlias(true);

        pY=500;
        score =0;
        life_count=3;

        //Criar os inimigos
        for (int c =0; c<2;c++){
            Enemy obj = new Enemy();
            obj.x=-60;
            obj.y=0;
            obj.value =new Random().nextInt(5);

            EConteiner.add(obj);
            conteiner_count++;
            Log.e("Conteiner", String.valueOf(conteiner_count));
        }
    }

    public void SelectImage(int i) {
        switch (i){
            default:

                //Caso nao esteja em nenhum local espcial
                player= BitmapFactory.decodeResource(getResources(),R.drawable.player_ship_b);
                bgImage=BitmapFactory.decodeResource(getResources(),R.drawable.bg_b);
                enemys[0]=BitmapFactory.decodeResource(getResources(),R.drawable.eb1);
                enemys[1]=BitmapFactory.decodeResource(getResources(),R.drawable.eb2);
                enemys[2]=BitmapFactory.decodeResource(getResources(),R.drawable.eb3);
                enemys[3]=BitmapFactory.decodeResource(getResources(),R.drawable.eb4);
                enemys[4]=BitmapFactory.decodeResource(getResources(),R.drawable.eb5);
                break;

            case 1:

                //Caso esteja na ESTG
                player= BitmapFactory.decodeResource(getResources(),R.drawable.player_ship_1);
                bgImage=BitmapFactory.decodeResource(getResources(),R.drawable.bg_1);
                enemys[0]=BitmapFactory.decodeResource(getResources(),R.drawable.e11);
                enemys[1]=BitmapFactory.decodeResource(getResources(),R.drawable.e12);
                enemys[2]=BitmapFactory.decodeResource(getResources(),R.drawable.e13);
                enemys[3]=BitmapFactory.decodeResource(getResources(),R.drawable.e14);
                enemys[4]=BitmapFactory.decodeResource(getResources(),R.drawable.e15);
                break;

            case 2:

                //Caso estaja no Bom Jesus
                player= BitmapFactory.decodeResource(getResources(),R.drawable.player_ship_2);
                bgImage=BitmapFactory.decodeResource(getResources(),R.drawable.bg_2);
                enemys[0]=BitmapFactory.decodeResource(getResources(),R.drawable.e21);
                enemys[1]=BitmapFactory.decodeResource(getResources(),R.drawable.e22);
                enemys[2]=BitmapFactory.decodeResource(getResources(),R.drawable.e23);
                enemys[3]=BitmapFactory.decodeResource(getResources(),R.drawable.e24);
                enemys[4]=BitmapFactory.decodeResource(getResources(),R.drawable.e25);
                break;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        //Ir buscar o tamanho da tela
        canvasWidth=canvas.getWidth();
        canvasHeigth=canvas.getHeight();

        canvas.drawBitmap(bgImage,0,0,null);
        //------------------------------------------------------------------------------------//

        //Mexer os jogador
        int minBirdY = player.getHeight();
        int maxBirdY = canvasHeigth-player.getHeight()*3;

        pY+=pspeed;

        if(pY<minBirdY) pY = minBirdY;
        if(pY>maxBirdY) pY=maxBirdY;

        if(touch_flg){
            pspeed =-15;
        }else{
            pspeed+=2;
        }
        canvas.drawBitmap(player,pX,pY,null);
        //------------------------------------------------------------------------------------//

        //Mexer os Inimigos
        for(int i = 0; i<conteiner_count;i++){
            EConteiner.get(i).x-=espeed;
            if(hitCheck(EConteiner.get(i).x,EConteiner.get(i).y,enemys[EConteiner.get(i).value].getHeight(),enemys[EConteiner.get(i).value].getWidth())){
                playhit();
                EConteiner.get(i).x=-100;
                life_count--;
                if(life_count==0){
                    Intent intent = new Intent(getContext(),Game_Over.class);
                    getContext().startActivity(intent);
                }
            }
            if(EConteiner.get(i).x<-60){
                getnewy(EConteiner.get(i),maxBirdY,minBirdY,i);
            }

            canvas.drawBitmap(enemys[EConteiner.get(i).value],EConteiner.get(i).x,EConteiner.get(i).y,null);
        }
        //------------------------------------------------------------------------------------//

        //Mexer os ECGM's
        if(!waitbool){
            ecgmx-=ecgmspeed;
            if(hitCheck(ecgmx,ecgmy, ecgm.getHeight(),ecgm.getWidth())){
                playpoint();
                ecgmx=-100;
                score+=2;
                if(score %4 ==0){
                    level++;
                    espeed+=level;
                    ecgmspeed -= (ecgmspeed*0.1);
                    playlevel();
                    Enemy obj = new Enemy();
                    obj.x=-60;
                    obj.y=0;
                    obj.value =new Random().nextInt(5);
                    if(level<=4){
                        EConteiner.add(obj);
                        conteiner_count++;
                        Log.e("Level", String.valueOf("Level UP"));
                    }
                }
            }
            if( ecgmx<-60){
                waitbool=true;
                ecgmx=canvasWidth+200;
                ecgmy=(int) Math.floor(Math.random()*(maxBirdY-minBirdY))+minBirdY;
            }
            canvas.drawBitmap(ecgm,ecgmx,ecgmy,null);
        }else{
            waitcount++;
        }

        canvas.drawText("Score : " + score,60,95,scorePaint);
        canvas.drawText("Level : " + level,450,95,levelPaint);
        //----------------------------------------------------------------------------------//

        //Vida
        for (int i=0;i<3;i++ ){
            int x = (int)(750+life[0].getWidth()*1.5*i);
            int y = 50;

            if(i<life_count){
                canvas.drawBitmap(life[0],x,y,null);
            }else{
                canvas.drawBitmap(life[1],x,y,null);
            }
        }
        //--------------------------------------------------------------------------------//

        if(waitcount>100*level){
            waitbool=false;
            waitcount=0;
        }
    }

    //Função que verifica se existe a colisao
    public boolean hitCheck(int x,int y, float h,float w){
        h=h/2;
        w=2/2;
        float distance= (float) Math.sqrt((y - pY) * (y - pY) + (x - pX) * (x - pX));
        float total = (float) (Math.sqrt((h*h)+(w*w))+Math.sqrt((player.getHeight()/2*player.getHeight()/2)+(player.getWidth()/2*player.getWidth()/2)));
        total -= total*0.1;
        if (distance<=total){
            return true;
        }
        return false;
    }

    //Funçao que indica posição é aspecto dos inimgos novos
    public void getnewy(Enemy obj,int maxBirdY,int minBirdY, int i){
        obj.value = new Random().nextInt(5);
        obj.x=canvasWidth+200;
        obj.y=(int) Math.floor(Math.random()*(maxBirdY-minBirdY))+minBirdY;
        if(i!=0){
            if(DistCheck(obj.y,EConteiner.get(i-1).y,enemys[obj.value].getWidth(),enemys[EConteiner.get(i-1).value].getHeight())){
                getnewy(obj,maxBirdY,minBirdY,i);
            }
        }
    }

    //Função que verifica se os inimigos nao estao sobrepostos
    public boolean DistCheck(int y1,int y2, float h1,float h2) {
        h1 /= 2;
        h2 /= 2;
        float distance = (float) Math.sqrt((y1 - y2) * (y1 - y2));
        float total = (float) h1 + h2;
        if (distance <= total) {
            return true;
        }
        return false;
    }

    //Funçoes para activar os efeitos sonoros
    public void playhit(){
        soundPool.play(hitsound,1.0f,1.0f,2,0,1.0f);
    }
    public void playpoint(){
        soundPool.play(pointsound,1.0f,1.0f,3,0,1.0f);
    }
    public void playlevel(){
        soundPool.play(levelsound,1.5f,1.5f,1,0,1.0f);
    }


}