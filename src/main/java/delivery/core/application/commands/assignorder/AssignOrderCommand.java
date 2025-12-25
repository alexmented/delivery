package delivery.core.application.commands.assignorder;

import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import libs.errs.Error;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssignOrderCommand {
    public static Result<AssignOrderCommand, Error> create() {
        return Result.success(new AssignOrderCommand());
    }
}
