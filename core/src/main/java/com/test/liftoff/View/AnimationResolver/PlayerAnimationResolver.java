package com.test.liftoff.View.AnimationResolver;

import com.test.liftoff.Enums.AnimationType;
import com.test.liftoff.Enums.EntityState;
import com.test.liftoff.Model.Entity.Entity;

public class PlayerAnimationResolver implements AnimationResolver {


    @Override
    public AnimationType getAnimation(Entity entity) {
        // The player just uses its internal state exactly like before
        switch (entity.getCurrentState()) {
            case RUNNING: return AnimationType.KnightRun;
            case JUMPING: return AnimationType.KnightRegularJump;
            case DOUBLE_JUMPING: return AnimationType.KnightDoubleJump;
            case FALLING: return AnimationType.KnightFall;
            case LANDING: return AnimationType.KnightRegularLanding;
            case DASHING: return AnimationType.KnightDash;
            case ATTACKING: return AnimationType.KnightNailSlash;
            case POGO_ATTACKING: return AnimationType.KnightPogo;
            case WALL_SLIDING: return AnimationType.KnightWallSlide;
            case WALL_JUMPING: return AnimationType.KnightWallJump;
            case FOCUS_START: return AnimationType.KnightFocusStart;
            case FOCUS_LOOPING: return AnimationType.KnightFocusLoop;
            case FOCUS_GET: return AnimationType.KnightFocusGet;
            case FOCUS_END: return AnimationType.KnightFocusEnd;
            case IDLE:
            default: return AnimationType.KnightIdle;
        }
    }
}
