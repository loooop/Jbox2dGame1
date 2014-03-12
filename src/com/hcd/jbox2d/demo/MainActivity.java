package com.hcd.jbox2d.demo;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private final static int RATE = 10;
	private World world;
	private Body m_ball,m_platform,m_triangle, m_box;
	private float timeStep;
	private int iterations;
	private Handler mHandler;
	private Jbox2dView myView;
	private Vec2[] vecs;
	private float screenWidth, screenHeight;
	
	class Jbox2dView extends View {

		float x, y;
		Canvas canvas;
		Paint paint;
		public Jbox2dView(Context context) {
			super(context);
			paint = new Paint();
		}
		
		private void drawBall(float x, float y, float radius) {
			paint.setAntiAlias(true);
			paint.setColor(Color.GREEN);
			canvas.drawCircle(x *RATE, y * RATE, radius, paint);
		}
 		
		private void drawTriangle(float x, float y, float angle, Vec2[] vecs) {
			Path path = new Path();
			Vec2[] tmp = new Vec2[3];
			for (int i = 0 ; i < 3; i++) {
				//tmp[i].x = vecs[i].x;//(float) (vecs[i].x * Math.cos(angle) - vecs[i].y * Math.sin(angle));
				//tmp[i].y = vecs[i].y;//(float) (vecs[i].x * Math.sin(angle) + vecs[i].y * Math.cos(angle));
				//tmp[i] = vecs[i];
				float vecX = (float) (vecs[i].x * Math.cos(angle) - vecs[i].y * Math.sin(angle));
				float vecY = (float) (vecs[i].x * Math.sin(angle) + vecs[i].y * Math.cos(angle));
				Vec2 vectmp = new Vec2(vecX, vecY);
				tmp[i] = vectmp;
			}
			
	        path.moveTo((x + tmp[0].x) * RATE, (y + tmp[0].y) * RATE);// �˵�Ϊ����ε����  
	        path.lineTo((x + tmp[1].x) * RATE, (y + tmp[1].y) * RATE);  
	        path.lineTo((x + tmp[2].x) * RATE, (y + tmp[2].y) * RATE); 
	        path.close(); // ʹ��Щ�㹹�ɷ�յĶ����  
	        canvas.drawPath(path, paint); 
		}
		
		private void drawPlatform(float x, float y, float width, float height) {
			paint.setAntiAlias(true);
			paint.setColor(Color.DKGRAY);
			canvas.drawRect(x * RATE, y * RATE, x * RATE + width, y * RATE + height, paint);
		}
		
		private void drawBox(float x, float y, float width, float height, float angle) {
			paint.setAntiAlias(true);
			paint.setColor(Color.GREEN);
			Path path = new Path();
			Vec2[] vecs = new Vec2[4];
			vecs[0] = new Vec2(50 / RATE, 40 / RATE);
			vecs[1] = new Vec2(-50 / RATE, 40 / RATE);
			vecs[2] = new Vec2(-50 / RATE, -40 / RATE);
			vecs[3] = new Vec2(50 / RATE, -40 / RATE);
			
			Vec2[] tmp = new Vec2[4];
			
			for (int i = 0 ; i < 4; i++) {
				//tmp[i].x = vecs[i].x;//(float) (vecs[i].x * Math.cos(angle) - vecs[i].y * Math.sin(angle));
				//tmp[i].y = vecs[i].y;//(float) (vecs[i].x * Math.sin(angle) + vecs[i].y * Math.cos(angle));
				//tmp[i] = vecs[i];
				float vecX = (float) (vecs[i].x * Math.cos(angle) - vecs[i].y * Math.sin(angle));
				float vecY = (float) (vecs[i].x * Math.sin(angle) + vecs[i].y * Math.cos(angle));
				Vec2 vectmp = new Vec2(vecX, vecY);
				tmp[i] = vectmp;
			}
			
	        path.moveTo((x + tmp[0].x) * RATE, (y + tmp[0].y) * RATE);// �˵�Ϊ����ε����  
	        path.lineTo((x + tmp[1].x) * RATE, (y + tmp[1].y) * RATE);  
	        path.lineTo((x + tmp[2].x) * RATE, (y + tmp[2].y) * RATE); 
	        path.lineTo((x + tmp[3].x) * RATE, (y + tmp[3].y) * RATE); 
	        path.close(); // ʹ��Щ�㹹�ɷ�յĶ����  
	        canvas.drawPath(path, paint); 
			//canvas.drawRect((x - width / 2), (y - height / 2), (x + width / 2), (y + height / 2) , paint);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			this.canvas = canvas;
			drawBall(m_ball.getPosition().x, m_ball.getPosition().y, 20);
			Log.i("dsds", m_triangle.getAngle()+"");
			drawTriangle(m_triangle.getPosition().x, m_triangle.getPosition().y, m_triangle.getAngle(), vecs);
			drawPlatform(m_platform.getPosition().x, m_platform.getPosition().y-10/RATE, screenWidth / 2, 20);
			drawBox(m_box.getPosition().x, m_box.getPosition().y, 100, 80, m_box.getAngle());
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // ȥtitle
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȫ��
 
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
        Vec2 gravity = new Vec2(0.0f, 10.0f); // ������������ʾ��ǰ������������򣬵�һ������Ϊˮƽ���򣬸���Ϊ��������Ϊ�ҡ��ڶ���������ʾ��ֱ����
        world = new World(gravity);
        createBall(200, screenHeight / 2 + 20, 20);
        createPlatform(0, screenHeight / 2, screenWidth / 2, 10);
        createTriangle();
        createBorder();
        createBox(260, 350, 100, 80);
        myView = new Jbox2dView(this);
        timeStep = 1.0f / 60.0f; // ����Ƶ��
        iterations = 10; // �������
        setContentView(myView);
        mHandler = new Handler();
        mHandler.post(update);
	}
	
	private Runnable update = new Runnable() {
		
		@Override
		public void run() {
			world.step(timeStep, iterations, iterations);
			myView.invalidate();
			
			mHandler.postDelayed(update, (long)timeStep * 1000);
		}
	};

	private void createBall(float x, float y, float radius) {
		//���������״��һ��Բ�ε�
		CircleShape shape = new CircleShape();
		//����Բ������뾶
		shape.setRadius(radius / RATE);
		
		//���ø������һЩ�̶�����
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.friction = 0.1f;
		fd.restitution = 1.5f;
		
		//������������Ķ���
		BodyDef bd = new BodyDef();
		bd.position.set(x / RATE, y / RATE);
		//��������ʱ���Զ���
		bd.type = BodyType.DYNAMIC;
		
		//����bodydef������������
		m_ball = world.createBody(bd);
		//������������Ĺ̶�һЩ����
		m_ball.createFixture(fd);
	}
	
	private void createPlatform(float x, float y, float width, float height){
		
		PolygonShape ps = new PolygonShape();
		//���óɾ��Σ�ע�����������������ֱ��Ǵ˾��γ����һ��
		ps.setAsBox(width / RATE, height / RATE);
		FixtureDef fd = new FixtureDef();
		fd.friction = 1.0f;
		fd.restitution = 0.5f;
		fd.shape = ps;
		
		BodyDef bd = new BodyDef();
		bd.position = new Vec2(x / RATE, y / RATE);
		m_platform = world.createBody(bd);
		m_platform.createFixture(fd);
	}
	
	private void createTriangle () {
		PolygonShape triangle = new PolygonShape();
		vecs = new Vec2[3];
		vecs[0] = new Vec2(0.0f, 5.0f);
		vecs[1] = new Vec2(3.0f, -4.0f);
		vecs[2] = new Vec2(-3.0f, -2.0f);
		triangle.set(vecs, 3);
		
		FixtureDef fd = new FixtureDef();
		fd.shape = triangle;
		fd.restitution = 0.6f;
		fd.density = 2.0f;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(30.0f, 10.0f);
		bd.angle = 0.0f;
		
		m_triangle = world.createBody(bd);
		m_triangle.createFixture(fd);
	}
	
	private void createBorder() {
		
		BodyDef  bd = new BodyDef();
		Body border = world.createBody(bd);
		EdgeShape es = new EdgeShape();
		es.set(new Vec2(0, 0), new Vec2(0, screenHeight / RATE));
		
		border.createFixture(es, 0.0f);
		
		es.set(new Vec2(screenWidth / RATE, 0), new Vec2(screenWidth / RATE, screenHeight / RATE));
		border.createFixture(es, 0.0f);
		
		es.set(new Vec2(0, screenHeight / RATE), new Vec2(screenWidth / RATE, screenHeight / RATE));
		border.createFixture(es, 0.0f);
		
		es.set(new Vec2(0, 0), new Vec2(screenWidth / RATE, 0));
		border.createFixture(es, 0.0f);
	}
	
	private void createBox(float x, float y, float width, float height) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / RATE, height / 2 / RATE);
		
		FixtureDef fd = new FixtureDef();
		fd.friction = 1.0f;
		fd.restitution = 0.5f;
		fd.shape = shape;
		fd.density = 0.3f;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = new Vec2(x / RATE, y / RATE);
		m_box = world.createBody(bd);
		m_box.createFixture(fd);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
