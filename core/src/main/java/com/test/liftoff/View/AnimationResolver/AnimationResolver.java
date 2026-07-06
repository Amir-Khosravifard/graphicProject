package com.test.liftoff.View.AnimationResolver;

import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Entity;

public interface AnimationResolver {
    AnimationType getAnimation(Entity entity);
}
