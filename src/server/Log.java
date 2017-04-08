package server;

import java.util.ArrayList;

public class Log {

    public static class BackupLog {
        private String fileId;
        private int chunkNo;
        private int replication;
        private int target; //desired replication
        private boolean present;
        private ArrayList<Integer> peers;

        public BackupLog(String fileId, int chunkNo, int target){
            this.fileId = fileId;
            this.chunkNo = chunkNo;
            this.target = target;
            this.replication = 1;
            this.present = true;
            this.peers = new ArrayList<>();
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public int getChunkNo() {
            return chunkNo;
        }

        public void setChunkNo(int chunkNo) {
            this.chunkNo = chunkNo;
        }

        public int getReplication() {
            return replication;
        }

        public void setReplication(int replication) {
            this.replication = replication;
        }

        public void incReplication() {
            this.replication++;
        }

        public void decReplication() {
            this.replication--;
        }

        public boolean isPresent() {
            return present;
        }

        public void setPresent(boolean present) {
            this.present = present;
        }

        public ArrayList<Integer> getPeers() {
            return peers;
        }

        public void setPeers(ArrayList<Integer> peers) {
            this.peers = peers;
        }
    }

    public static class LocalLog {

        private String fileId;
        private int chunkNo;
        private int replication;
        private int desiredReplication;

        public LocalLog(String fileId, int chunkNo, int desiredReplication) {
            this.fileId = fileId;
            this.chunkNo = chunkNo;
            this.desiredReplication = desiredReplication;
            this.replication = 1;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public int getChunkNo() {
            return chunkNo;
        }

        public void setChunkNo(int chunkNo) {
            this.chunkNo = chunkNo;
        }

        public int getReplication() {
            return replication;
        }

        public void setReplication(int replication) {
            this.replication = replication;
        }

        public void incReplication() {
            this.replication++;
        }

        public void decReplication() {
            this.replication--;
        }

        public int getDesiredReplication() {
            return desiredReplication;
        }

        public void setDesiredReplication(int desiredReplication) {
            this.desiredReplication = desiredReplication;
        }
    }

    public static ArrayList<BackupLog> bLogs;
    public static ArrayList<LocalLog> lLogs;

    public Log(){}


}
