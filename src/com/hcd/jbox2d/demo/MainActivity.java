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

import com.example.jbox2dgame1.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	//物理屏幕与物理世界的比例px/m
	private final static int RATE = 10;
	private World world;
	//世界中的一些物体
	private Body m_ball, m_platform, m_triangle, m_box;
	//模拟的频率
	private float timeStep;
	//迭代越大，模拟越精确，但性能越低
	private int iterations;
	private Handler mHandler;
	private Jbox2dView myView;
	private Vec2[] vecs;
	//屏幕的宽度与高度
	private float screenWidth, screenHeight;
	private Polygon polygon1;

	class Jbox2dView extends View {

		private float mov_x, mov_y, up_x, up_y;
		private Canvas canvas;
		private Paint paint;

		public Jbox2dView(Context context) {
			super(context);
			paint = new Paint();
		}

		private void drawBall(float x, float y, float radius) {
			paint.setAntiAlias(true);
			paint.setColor(Color.rgb(111, 100, 99));
			canvas.drawCircle(x * RATE, y * RATE, radius, paint);
		}

		private void drawTriangle(float x, float y, float angle, Vec2[] vecs) {
			Path path = new Path();
			Vec2[] tmp = new Vec2[3];
			for (int i = 0; i < 3; i++) {
				// tmp[i].x = vecs[i].x;//(float) (vecs[i].x * Math.cos(angle) -
				// vecs[i].y * Math.sin(angle));
				// tmp[i].y = vecs[i].y;//(float) (vecs[i].x * Math.sin(angle) +
				// vecs[i].y * Math.cos(angle));
				// tmp[i] = vecs[i];
				float vecX = (float) (vecs[i].x * Math.cos(angle) - vecs[i].y
						* Math.sin(angle));
				float vecY = (float) (vecs[i].x * Math.sin(angle) + vecs[i].y
						* Math.cos(angle));
				Vec2 vectmp = new Vec2(vecX, vecY);
				tmp[i] = vectmp;
			}

			path.moveTo((x + tmp[0].x) * RATE, (y + tmp[0].y) * RATE);// 此点为多边形的起点
			path.lineTo((x + tmp[1].x) * RATE, (y + tmp[1].y) * RATE);
			path.lineTo((x + tmp[2].x) * RATE, (y + tmp[2].y) * RATE);
			path.close(); // 使这些点构成封闭的多边形
			paint.setColor(Color.rgb(10, 100, 99));
			canvas.drawPath(path, paint);
		}

		private void drawPlatform(float x, float y, float width, float height) {
			paint.setAntiAlias(true);
			paint.setColor(Color.DKGRAY);
			canvas.drawRect(x * RATE, y * RATE, x * RATE + width, y * RATE
					+ height, paint);
		}

		private void drawBox(float x, float y, float width, float height,
				float angle) {
			paint.setAntiAlias(true);
			paint.setColor(Color.rgb(111, 10, 99));
			Path path = new Path();
			Vec2[] vecs = new Vec2[4];
			vecs[0] = new Vec2(50 / RATE, 40 / RATE);
			vecs[1] = new Vec2(-50 / RATE, 40 / RATE);
			vecs[2] = new Vec2(-50 / RATE, -40 / RATE);
			vecs[3] = new Vec2(50 / RATE, -40 / RATE);

			Vec2[] tmp = new Vec2[4];

			for (int i = 0; i < 4; i++) {
				// tmp[i].x = vecs[i].x;//(float) (vecs[i].x * Math.cos(angle) -
				// vecs[i].y * Math.sin(angle));
				// tmp[i].y = vecs[i].y;//(float) (vecs[i].x * Math.sin(angle) +
				// vecs[i].y * Math.cos(angle));
				// tmp[i] = vecs[i];
				float vecX = (float) (vecs[i].x * Math.cos(angle) - vecs[i].y
						* Math.sin(angle));
				float vecY = (float) (vecs[i].x * Math.sin(angle) + vecs[i].y
						* Math.cos(angle));
				Vec2 vectmp = new Vec2(vecX, vecY);
				tmp[i] = vectmp;
			}

			path.moveTo((x + tmp[0].x) * RATE, (y + tmp[0].y) * RATE);// 此点为多边形的起点
			path.lineTo((x + tmp[1].x) * RATE, (y + tmp[1].y) * RATE);
			path.lineTo((x + tmp[2].x) * RATE, (y + tmp[2].y) * RATE);
			path.lineTo((x + tmp[3].x) * RATE, (y + tmp[3].y) * RATE);
			path.close(); // 使这些点构成封闭的多边形
			canvas.drawPath(path, paint);
			// canvas.drawRect((x - width / 2), (y - height / 2), (x + width /
			// 2), (y + height / 2) , paint);
		}

		private void drawLine(float x1, float y1, float x2, float y2) {
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			canvas.drawLine(x1, y1, x2, y2, paint);
		}

		private void drawPolygon(Polygon polygon) {
			paint.setAntiAlias(true);
			paint.setColor(Color.rgb(111, 100, 10));
			Path path = new Path();
			Vec2[] vecs = polygon.getVecs();
			Vec2[] tmp = new Vec2[polygon.getEdge()];
			for (int i = 0; i < polygon.getEdge(); i++) {
				float vecX = (float) (vecs[i].x * Math.cos(polygon.getAngle()) - vecs[i].y
						* Math.sin(polygon.getAngle()));
				float vecY = (float) (vecs[i].x * Math.sin(polygon.getAngle()) + vecs[i].y
						* Math.cos(polygon.getAngle()));
				tmp[i] = new Vec2(vecX, vecY);
			}

			path.moveTo((polygon.getX() + tmp[0].x) * RATE,
					(polygon.getY() + tmp[0].y) * RATE);// 此点为多边形的起点
			for (int i = 1; i < polygon.getEdge(); i++) {
				path.lineTo((polygon.getX() + tmp[i].x) * RATE,
						(polygon.getY() + tmp[i].y) * RATE);
			}
			path.close(); // 使这些点构成封闭的多边形
			canvas.drawPath(path, paint);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			this.canvas = canvas;
			drawBall(m_ball.getPosition().x, m_ball.getPosition().y, 20);
			Log.i("dsds", m_triangle.getAngle() + "");
			drawTriangle(m_triangle.getPosition().x,
					m_triangle.getPosition().y, m_triangle.getAngle(), vecs);
			drawPlatform(m_platform.getPosition().x, m_platform.getPosition().y
					- 10 / RATE, screenWidth / 2, 20);
			drawBox(m_box.getPosition().x, m_box.getPosition().y, 100, 80,
					m_box.getAngle());
			drawLine(mov_x, mov_y, up_x, up_y);
			drawPolygon(polygon1);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {// 如果拖动
				canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);// 画线
				invalidate();
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) {// 如果点击
				mov_x = (int) event.getX();
				mov_y = (int) event.getY();
				canvas.drawPoint(mov_x, mov_y, paint);// 画点
				invalidate();
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				up_x = event.getX();
				up_y = event.getY();
			}
			// mov_x = (int) event.getX();
			// mov_y = (int) event.getY();
			return true;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 去title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels;
		screenHeight = metric.heightPixels;
		Vec2 gravity = new Vec2(0.0f, 10.0f); // 向量，用来标示当前世界的重力方向，第一个参数为水平方向，负数为做，正数为右。第二个参数表示垂直方向
		world = new World(gravity);
		createBall(200, screenHeight / 2 + 20, 20);
		createPlatform(0, screenHeight / 2, screenWidth / 2, 10);
		createTriangle();
		createBorder();
		createBox(240, 350, 100, 80);
		createPolygon1();
		myView = new Jbox2dView(this);
		timeStep = 1.0f / 60.0f; // 定义频率
		iterations = 10; // 定义迭代
		setContentView(myView);
		mHandler = new Handler();
		mHandler.post(update);
	}

	private Runnable update = new Runnable() {

		@Override
		public void run() {
			world.step(timeStep, iterations, iterations);
			myView.invalidate();

			mHandler.postDelayed(update, (long) timeStep * 1000);
		}
	};

	private void createBall(float x, float y, float radius) {
		// 定义球的形状是一个圆形的
		CircleShape shape = new CircleShape();
		// 设置圆形球体半径
		shape.setRadius(radius / RATE);

		// 设置该物体的一些固定属性
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.friction = 0.1f;
		fd.restitution = 1.5f;

		// 设置球形物体的定义
		BodyDef bd = new BodyDef();
		bd.position.set(x / RATE, y / RATE);
		// 标明物体时可以动的
		bd.type = BodyType.DYNAMIC;

		// 根据bodydef创建球形物体
		m_ball = world.createBody(bd);
		// 设置球形物体的固定一些属性
		m_ball.createFixture(fd);
	}

	private void createPlatform(float x, float y, float width, float height) {

		PolygonShape ps = new PolygonShape();
		// 设置成矩形，注意这里是两个参数分别是此矩形长宽的一半
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

	private void createTriangle() {
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
		bd.position.set(36.0f, 10.0f);
		bd.angle = 0.0f;

		m_triangle = world.createBody(bd);
		m_triangle.createFixture(fd);
	}

	private void createBorder() {

		BodyDef bd = new BodyDef();
		Body border = world.createBody(bd);
		EdgeShape es = new EdgeShape();
		es.set(new Vec2(0, 0), new Vec2(0, screenHeight / RATE));

		border.createFixture(es, 0.0f);

		es.set(new Vec2(screenWidth / RATE, 0), new Vec2(screenWidth / RATE,
				screenHeight / RATE));
		border.createFixture(es, 0.0f);

		es.set(new Vec2(0, screenHeight / RATE), new Vec2(screenWidth / RATE,
				screenHeight / RATE));
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

	private void createPolygon1() {
		Vec2[] vecs = new Vec2[6];
		for (int i = 0; i < 6; i++) {
			float vecX = (float) (30 / RATE * Math.cos(i * Math.PI / 3) - 0 * Math
					.sin(i * Math.PI / 3));
			float vecY = (float) (30 / RATE * Math.sin(i * Math.PI / 3) + 0 * Math
					.cos(i * Math.PI / 3));
			vecs[i] = new Vec2(vecX, vecY);
		}
		polygon1 = new Polygon(world, 300 / RATE, 100 / RATE, vecs, 6, 0.5f,
				0.5f, 0.5f, 0.0f);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {

		/**
		 * 强制变成竖屏，不能变成横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

}
