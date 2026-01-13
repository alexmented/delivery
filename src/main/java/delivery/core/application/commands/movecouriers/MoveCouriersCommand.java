package delivery.core.application.commands.movecouriers;

import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveCouriersCommand {
    public static Result<MoveCouriersCommand, libs.errs.Error> create() {
        return Result.success(new MoveCouriersCommand());
    }
}
