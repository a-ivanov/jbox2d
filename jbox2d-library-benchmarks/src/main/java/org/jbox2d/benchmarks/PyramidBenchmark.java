package org.jbox2d.benchmarks;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
public class PyramidBenchmark {
  private final float TIME_STEP = 1f / 60f;
  private final int VELOCITY_ITERATIONS = 8;
  private final int POSITION_ITERATIONS = 3;

  @Param({"20", "40", "80", "160"})
  private int count;

  private World world;

  @Setup(Level.Iteration)
  public void setup() {
    world = createWorld();
  }

  @Benchmark
  public void step() {
    world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
  }

  private World createWorld() {
    World world = new World(new Vec2(0, -10));

    {
      BodyDef bd = new BodyDef();
      Body ground = world.createBody(bd);

      EdgeShape shape = new EdgeShape();
      shape.set(new Vec2(-40.0f, 0f), new Vec2(40.0f, 0f));
      ground.createFixture(shape, 0.0f);
    }

    {
      float a = .5f;
      PolygonShape shape = new PolygonShape();
      shape.setAsBox(a, a);

      Vec2 x = new Vec2(-7.0f, 0.75f);
      Vec2 y = new Vec2();
      Vec2 deltaX = new Vec2(0.5625f, 1.25f);
      Vec2 deltaY = new Vec2(1.125f, 0.0f);

      for (int i = 0; i < count; ++i) {
        y.set(x);

        for (int j = i; j < count; ++j) {
          BodyDef bd = new BodyDef();
          bd.type = BodyType.DYNAMIC;
          bd.position.set(y);
          Body body = world.createBody(bd);
          body.createFixture(shape, 5.0f);
          y.addLocal(deltaY);
        }

        x.addLocal(deltaX);
      }
    }

    return world;
  }
}
