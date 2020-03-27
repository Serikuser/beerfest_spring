package by.siarhei.beerfest.command;

import by.siarhei.beerfest.command.api.ActionCommand;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Utility class for define command.
 */

public class CommandProvider {

    private static final String PARAMETER_COMMAND = "command";

    private  Map<CommandType, ActionCommand> commands;

    public CommandProvider(Map<CommandType,ActionCommand> commands) {
        this.commands = commands;
    }
    /**
     * Defines a command by its string representation that gets from request.
     * But there must be a corresponding element in enum {@code CommandType}
     *
     * @param request object that contain request.
     * @return {@code ActionCommand} object
     */
    public ActionCommand defineCommand(HttpServletRequest request) {
        String action = request.getParameter(PARAMETER_COMMAND);
        CommandType currentType = CommandType.valueOf(action.toUpperCase());
        return commands.get(currentType);
    }
}
