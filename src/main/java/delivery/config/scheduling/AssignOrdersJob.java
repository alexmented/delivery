package delivery.config.scheduling;

import delivery.core.application.commands.assignorder.AssignOrderCommand;
import delivery.core.application.commands.assignorder.AssignOrderCommandHandler;
import libs.errs.Result;
import libs.errs.Error;
import libs.errs.UnitResult;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssignOrdersJob implements Job {
    private final AssignOrderCommandHandler commandHandler;

    @Autowired
    public AssignOrdersJob(AssignOrderCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(JobExecutionContext context) {
        Result<AssignOrderCommand, Error> commandResult = AssignOrderCommand.create();
        if (commandResult.isFailure()) {
            return;
        }

        UnitResult<Error> result = commandHandler.handle(commandResult.getValue());
        if (result.isFailure()) {
            System.err.println("AssignOrdersJob failed: " + result.getError());
        }
    }
}
