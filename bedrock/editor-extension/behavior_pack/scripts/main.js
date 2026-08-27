import * as editor from "@minecraft/server-editor";

const exportedBindings = Object.keys(editor).length;
console.warn(
  `[Gridelyx Studio] Bedrock Editor preview extension loaded (${exportedBindings} editor bindings visible)`,
);
