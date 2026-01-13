package delivery.config.scheduling;

import delivery.core.application.commands.movecouriers.MoveCouriersCommand;
import delivery.core.application.commands.movecouriers.MoveCouriersCommandHandler;
import libs.errs.Result;
import libs.errs.Error;
import libs.errs.UnitResult;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoveCouriersJob implements Job {
    private final MoveCouriersCommandHandler commandHandler;

    @Autowired
    public MoveCouriersJob(MoveCouriersCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("MoveCouriersJob started at: " + java.time.LocalDateTime.now());
        
        Result<MoveCouriersCommand, Error> commandResult = MoveCouriersCommand.create();
        if (commandResult.isFailure()) {
            System.err.println("Failed to create MoveCouriersCommand");
            return;
        }

        UnitResult<Error> result = commandHandler.handle(commandResult.getValue());
        if (result.isFailure()) {
            System.err.println("MoveCouriersJob failed: " + result.getError());
        } else {
            System.out.println("MoveCouriersJob completed successfully");
        }
    }
}
