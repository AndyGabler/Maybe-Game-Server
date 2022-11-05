package com.andronikus.gameserver.engine;

import com.andronikus.game.model.client.InputPurgeRequest;
import com.andronikus.game.model.client.InputRequest;
import com.andronikus.game.model.server.input.InputAcknowledgement;
import com.andronikus.gameserver.auth.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class InputAcknowledgementManager {

    /*
     * TODO
     * Technically, this could get bloated. Might be good to every few minutes just purge this. The worst that happens is
     * that a client that missed their ack and has not been sending their duplicate check just gets an input reprocessed.
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, Byte>> processedCache = new ConcurrentHashMap<>();
    private final ArrayList<InputAcknowledgement> acksForSend = new ArrayList<>();
    private final ConcurrentLinkedQueue<InputPurge> purgeQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<InputAcknowledgement> ackRenewalQueue = new ConcurrentLinkedQueue<>();

    public void registerAck(InputAcknowledgement acknowledgement) {
        ConcurrentHashMap<Long, Byte> cacheForSessionId = processedCache.computeIfAbsent(acknowledgement.getSessionId(), sessionId -> new ConcurrentHashMap<>());
        cacheForSessionId.put(acknowledgement.getInputId(), (byte) 0x01);
        acksForSend.add(acknowledgement);
    }

    public void purgeExpiredAndRequestedAcks(long gameStateVersion, long retentionTicks) {
        acksForSend.removeIf(ack -> {
            final long ackAgeTicks = gameStateVersion - ack.getCreatedGameStateVersion();
            return ackAgeTicks > retentionTicks;
        });
        // TODO techically might be a good idea to throttle the purge processing?
        final ArrayList<InputPurge> purgesToProcess = new ArrayList<>();
        InputPurge purge = purgeQueue.poll();
        while (purge != null) {
            purgesToProcess.add(purge);
            purge = purgeQueue.poll();
        }
        acksForSend.removeIf(ack ->
            purgesToProcess.stream().anyMatch(purgeToProcess ->
                ack.getInputId() == purgeToProcess.inputId &&
                ack.getSessionId().equalsIgnoreCase(purgeToProcess.sessionId)
            )
        );
    }

    public List<InputAcknowledgement> pollForAcks(int limit, long gameStateVersion) {
        /*
         * It's okay if a duplicate gets put in the ack queue, this means cache was cleared and will be cleaned up on purge
         * or ack purge request.
         */
        InputAcknowledgement renewedAck = ackRenewalQueue.poll();
        while (renewedAck != null) {
            renewedAck.setCreatedGameStateVersion(gameStateVersion);
            acksForSend.add(renewedAck);
            renewedAck = ackRenewalQueue.poll();
        }

        return acksForSend.stream().limit(limit).collect(Collectors.toList());
    }

    public void purgeInputs(List<InputPurgeRequest> purgeRequests, Session session) {
        purgeRequests.forEach(inputPurgeRequest -> {
            final InputPurge purge = new InputPurge();
            purge.inputId = inputPurgeRequest.getId();
            purge.sessionId = session.getId();
            purgeQueue.add(purge);
        });
    }

    /**
     * Check, from any thread, if the current input request is already known to the server. If it is, requeue the
     * acknowledgement since the client is clearly missing it.
     *
     * @param inputRequest The input request
     * @return True if the input is a duplicate
     */
    public boolean queueDuplicateAckResend(InputRequest inputRequest, String sessionId) {
        final ConcurrentHashMap<Long, Byte> inputCache = processedCache.get(sessionId);
        if (inputCache == null) {
            return false;
        }

        final boolean inputExists = inputCache.containsKey(inputRequest.getInputId());
        if (inputExists) {
            final InputAcknowledgement renewedAck = new InputAcknowledgement();
            renewedAck.setInputId(inputRequest.getInputId());
            renewedAck.setSessionId(sessionId);
            ackRenewalQueue.add(renewedAck);
        }
        return inputExists;
    }

    private static class InputPurge {
        private String sessionId;
        private long inputId;
    }
}
