.class public boolean
.super java/lang/Object

.method public <init>()V
  .limit locals 1

  aload_0
  invokespecial java/lang/Object/<init>()V
  return

.end method

.method public static main([Ljava/lang/String;)V
  .limit locals 1
  .limit stack  1

  invokestatic boolean/main()I
  pop
  return

.end method


.method public static ff()Z
  .limit locals 0
  .limit stack 1

  iconst_0
  invokestatic Runtime/printInt(I)V
  iconst_0
  ireturn

.end method

.method public static tt()Z
  .limit locals 0
  .limit stack 1

  iconst_1
  invokestatic Runtime/printInt(I)V
  iconst_1
  ireturn

.end method

.method public static main()I
  .limit locals 6
  .limit stack 13

  iconst_1
  istore_0
  iconst_0
  istore_1
  iload_1
  ifeq L0
  invokestatic boolean/ff()Z
  iconst_1
  goto L1
  L0:
  iconst_0
  L1:
  istore_2
  iload_0
  ifeq L2
  invokestatic boolean/ff()Z
  iconst_0
  goto L3
  L2:
  iconst_1
  L3:
  istore_3
  iload_0
  ifeq L4
  invokestatic boolean/tt()Z
  iconst_1
  goto L5
  L4:
  iconst_0
  L5:
  istore 4
  iload_1
  ifeq L6
  invokestatic boolean/tt()Z
  iconst_0
  goto L7
  L6:
  iconst_1
  L7:
  istore 5
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
