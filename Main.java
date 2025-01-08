import java.util.*;

class Process {
    int id;
    int burstTime;
    int arrivalTime;
    int priority;

    public Process(int id, int burstTime, int arrivalTime, int priority) {
        this.id = id;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
    }
}

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter details for process " + (i + 1) + ":");
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Priority (enter 0 if not used): ");
            int priority = scanner.nextInt();
            processes.add(new Process(i + 1, burstTime, arrivalTime, priority));
        }

        System.out.println("Choose Scheduling Algorithm:");
        System.out.println("1. First-Come, First-Served Scheduling");
        System.out.println("2. Shortest-Job-First Scheduling");
        System.out.println("3. Priority Scheduling");
        System.out.println("4. Round-Robin Scheduling");
        int choice = scanner.nextInt();

        boolean isPreemptive = false;
        if (choice == 2 || choice == 3) {
            System.out.print("Do you want Preemptive Scheduling? (yes=1 / no=0): ");
            int preemptiveChoice = scanner.nextInt();
            isPreemptive = preemptiveChoice == 1;
        }

        switch (choice) {
            case 1:
                fcfsScheduling(processes);
                break;
            case 2:
                if (isPreemptive) {
                    preemptiveSjfScheduling(processes);
                } else {
                    sjfScheduling(processes);
                }
                break;
            case 3:
                if (isPreemptive) {
                    preemptivePriorityScheduling(processes);
                } else {
                    priorityScheduling(processes);
                }
                break;
            case 4:
                System.out.print("Enter time quantum for Round-Robin: ");
                int quantum = scanner.nextInt();
                roundRobinScheduling(processes, quantum);
                break;
            default:
                System.out.println("Invalid choice!");
        }

        scanner.close();
    }

    private static void fcfsScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        int totalWaitingTime = 0;

        System.out.println("Gantt Chart:");
        for (Process p : processes) {
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime;
            }
            int waitingTime = currentTime - p.arrivalTime;
            totalWaitingTime += waitingTime;
            currentTime += p.burstTime;
            System.out.print("| P" + p.id + " ");
        }
        System.out.println("|");
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
    }

    private static void sjfScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.burstTime));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completed = 0;

        System.out.println("Gantt Chart:");
        while (completed < processes.size()) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !pq.contains(p)) {
                    pq.add(p);
                }
            }
            if (!pq.isEmpty()) {
                Process p = pq.poll();
                int waitingTime = currentTime - p.arrivalTime;
                totalWaitingTime += waitingTime;
                currentTime += p.burstTime;
                completed++;
                System.out.print("| P" + p.id + " ");
            } else {
                currentTime++;
            }
        }
        System.out.println("|");
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
    }

    private static void preemptiveSjfScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.burstTime));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completed = 0;
        Map<Process, Integer> remainingBurst = new HashMap<>();

        for (Process p : processes) {
            remainingBurst.put(p, p.burstTime);
        }

        System.out.println("Gantt Chart:");
        while (completed < processes.size()) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !pq.contains(p)) {
                    pq.add(p);
                }
            }
            if (!pq.isEmpty()) {
                Process p = pq.poll();
                System.out.print("| P" + p.id + " ");
                if (remainingBurst.get(p) > 1) {
                    remainingBurst.put(p, remainingBurst.get(p) - 1);
                    currentTime++;
                    pq.add(p);
                } else {
                    currentTime++;
                    completed++;
                }
            } else {
                currentTime++;
            }
        }
        System.out.println("|");
    }

    private static void priorityScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completed = 0;

        System.out.println("Gantt Chart:");
        while (completed < processes.size()) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !pq.contains(p)) {
                    pq.add(p);
                }
            }
            if (!pq.isEmpty()) {
                Process p = pq.poll();
                int waitingTime = currentTime - p.arrivalTime;
                totalWaitingTime += waitingTime;
                currentTime += p.burstTime;
                completed++;
                System.out.print("| P" + p.id + " ");
            } else {
                currentTime++;
            }
        }
        System.out.println("|");
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
    }

    private static void preemptivePriorityScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
        int currentTime = 0;
        int completed = 0;
        Map<Process, Integer> remainingBurst = new HashMap<>();

        for (Process p : processes) {
            remainingBurst.put(p, p.burstTime);
        }

        System.out.println("Gantt Chart:");
        while (completed < processes.size()) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !pq.contains(p)) {
                    pq.add(p);
                }
            }
            if (!pq.isEmpty()) {
                Process p = pq.poll();
                System.out.print("| P" + p.id + " ");
                if (remainingBurst.get(p) > 1) {
                    remainingBurst.put(p, remainingBurst.get(p) - 1);
                    currentTime++;
                    pq.add(p);
                } else {
                    currentTime++;
                    completed++;
                }
            } else {
                currentTime++;
            }
        }
        System.out.println("|");
    }

    private static void roundRobinScheduling(List<Process> processes, int quantum) {
        Queue<Process> queue = new LinkedList<>(processes);
        int currentTime = 0;
        int totalWaitingTime = 0;
        Map<Integer, Integer> remainingTime = new HashMap<>();

        for (Process p : processes) {
            remainingTime.put(p.id, p.burstTime);
        }

        System.out.println("Gantt Chart:");
        while (!queue.isEmpty()) {
            Process p = queue.poll();
            if (remainingTime.get(p.id) > quantum) {
                System.out.print("| P" + p.id + " ");
                currentTime += quantum;
                remainingTime.put(p.id, remainingTime.get(p.id) - quantum);
                queue.add(p);
            } else {
                System.out.print("| P" + p.id + " ");
                currentTime += remainingTime.get(p.id);
                int waitingTime = currentTime - p.arrivalTime - p.burstTime;
                totalWaitingTime += Math.max(waitingTime, 0);
                remainingTime.put(p.id, 0);
            }
        }
        System.out.println("|");
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
    }
}
