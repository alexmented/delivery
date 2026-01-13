package delivery.core.application.commands.createcourier;

import libs.errs.Err;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateCourierCommand {
    private final String name;
    private final int speed;

    public static Result<CreateCourierCommand, Error> create(String name, int speed) {
        var validation = UnitResult.combine(
                Err.againstNullOrEmpty(name, "name"),
                Err.againstZeroOrNegative(speed, "speed")
        );
        if (validation.isFailure()) return Result.failure(validation.getError());

        return Result.success(new CreateCourierCommand(name, speed));
    }
}
