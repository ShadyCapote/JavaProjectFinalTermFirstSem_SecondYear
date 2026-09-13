#include <stdio.h>
#include <stdlib.h>

int main() {
    FILE *cpu_info = fopen("/proc/cpuinfo", "r");

    if (cpu_info == NULL) {
        perror("Error: Hardware API pathway unreachable");
        return 1;
    }

    char line_buffer[256];

    printf("========================================\n");
    printf("      LAB 1.2: HARDWARE API METRIC\n");
    printf("           TELEMETRY OUTPUT\n");
    printf("========================================\n");

    for (int i = 0;
         i < 10 && fgets(line_buffer, sizeof(line_buffer), cpu_info) != NULL;
         i++) {
        printf("[API ABSTRACTION LAYER] %s", line_buffer);
    }

    fclose(cpu_info);

    return 0;
}
