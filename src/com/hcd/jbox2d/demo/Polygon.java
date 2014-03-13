package com.hcd.jbox2d.demo;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Polygon {

	//���������������ڵ�����
	private World world;
	//��������������
	private float x, y;
	//���ɶ���εĵ�
	private Vec2[] vecs;
	//����ĵ���ϵ��
	private float restitution;
	//������ܶ�
	private float density;
	//�������ת�ǶȺ������Ħ��
	private float angle, friction;
	//�Զ�����������ı���
	private int edge;
	private Body body;
	
	public Polygon(World world, float x, float y, Vec2[] vecs, int edge,  float restitution, float density, float friction, float angle) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.vecs = vecs;
		this.restitution = restitution;
		this.density = density;
		this.angle = angle;
		this.edge = edge;
		createBody();
	}
	/**
	 * �������������
	 */
	private void createBody() {
		//�����������״�Ƕ������״
		PolygonShape shape = new PolygonShape();
		shape.set(vecs, edge);
		
		//���ö���������һЩ�̶�����������
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
		bd.type = BodyType.DYNAMIC;
		bd.angle = angle;
		
		//����bodyDef�������嵽������
		body = world.createBody(bd);
		//���ú�����Ĺ̶�����
		body.createFixture(fd);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public float getX() {
		return body.getPosition().x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return body.getPosition().y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Vec2[] getVecs() {
		return vecs;
	}

	public void setVecs(Vec2[] vecs) {
		this.vecs = vecs;
	}

	public float getRestitution() {
		return restitution;
	}

	public void setRestitution(float restitution) {
		this.restitution = restitution;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public float getAngle() {
		return body.getAngle();
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getFriction() {
		return friction;
	}

	public void setFriction(float friction) {
		this.friction = friction;
	}

	public int getEdge() {
		return edge;
	}

	public void setEdge(int edge) {
		this.edge = edge;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
}