import { system, world } from "@minecraft/server";

const PREFIX = "gridelyx:";

export class GridelyxBedrockRuntime {
  constructor() {
    this.handlers = new Map();
    this.started = false;
    this.scriptEventHandler = undefined;
  }

  start() {
    if (this.started) {
      return;
    }
    this.started = true;
    this.registerBuiltIns();
    this.scriptEventHandler = (event) => this.receiveScriptEvent(event);
    system.afterEvents.scriptEventReceive.subscribe(this.scriptEventHandler);
    console.warn("[Gridelyx Studio] Bedrock runtime online");
  }

  stop() {
    if (!this.started) {
      return;
    }
    if (this.scriptEventHandler !== undefined) {
      system.afterEvents.scriptEventReceive.unsubscribe(this.scriptEventHandler);
    }
    this.scriptEventHandler = undefined;
    this.started = false;
  }

  on(action, handler) {
    if (typeof action !== "string" || action.length === 0) {
      throw new TypeError("Gridelyx action must be a non-empty string");
    }
    if (typeof handler !== "function") {
      throw new TypeError("Gridelyx handler must be a function");
    }
    this.handlers.set(action, handler);
    return this;
  }

  dispatch(action, payload, context = {}) {
    const handler = this.handlers.get(action);
    if (handler === undefined) {
      return {
        ok: false,
        action,
        error: `Unsupported Gridelyx Bedrock action: ${action}`,
      };
    }

    try {
      const value = handler(payload, context);
      return { ok: true, action, value };
    } catch (error) {
      return {
        ok: false,
        action,
        error: String(error?.stack ?? error),
      };
    }
  }

  registerBuiltIns() {
    this.on("ping", () => ({ product: "Gridelyx Studio", bridge: 2 }));
    this.on("capabilities", () => ({
      scripting: true,
      scriptEvents: true,
      nativeMemory: false,
      editorExtension: "preview",
      dedicatedServerNetwork: "optional-preview",
      protocol: "GLXB/2",
    }));
    this.on("announce", (payload) => {
      const text = typeof payload?.text === "string" ? payload.text : String(payload ?? "");
      world.sendMessage(`[Gridelyx Studio] ${text}`);
      return { delivered: true };
    });
  }

  receiveScriptEvent(event) {
    if (typeof event.id !== "string" || !event.id.startsWith(PREFIX)) {
      return;
    }

    const action = event.id.substring(PREFIX.length);
    let payload = {};
    if (event.message !== undefined && event.message !== "") {
      try {
        payload = JSON.parse(event.message);
      } catch {
        payload = { value: event.message };
      }
    }

    const result = this.dispatch(action, payload, {
      sourceBlock: event.sourceBlock,
      sourceEntity: event.sourceEntity,
      sourceType: event.sourceType,
    });
    console.warn(`[Gridelyx Studio] ${JSON.stringify(result)}`);
  }
}
