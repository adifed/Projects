BIN ?= bin
OBJ ?= obj

CFLAGS += -DSTRIDE=1024 -DSTRIDE_SHIFT=10
CFLAGS += -Iinclude -O2
LDFLAGS += -O2


obj-y += \
	args.c.o \
	utils.c.o \
	utils.S.o
objs = $(addprefix $(OBJ)/, $(obj-y))

.PHONY: all clean

all: $(BIN)/msbds 

$(OBJ)/%.c.o: %.c
	@echo "CC $<"
	@mkdir -p "$(dir $@)"
	@$(CC) -c $< -o $@ $(CFLAGS) -MT $@ -MMD -MP -MF $(@:.o=.d)

$(OBJ)/%.S.o: %.S
	@echo "AS $<"
	@mkdir -p "$(dir $@)"
	@$(CC) -c $< -o $@ $(CFLAGS) -MT $@ -MMD -MP -MF $(@:.o=.d)

$(BIN)/msbds: msbds.c $(objs)
	@echo "LD $@"
	@mkdir -p "$(dir $@)"
	@$(CC) $^ -o $@ $(CFLAGS) $(LDFLAGS) $(LIBS)


clean:
	@rm -rf $(OBJ) $(BIN)

