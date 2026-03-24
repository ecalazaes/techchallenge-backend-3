package com.techchallenge.history_service.consumer;

import com.techchallenge.history_service.config.RabbitConfig;
import com.techchallenge.history_service.dto.AppointmentEventDTO;
import com.techchallenge.history_service.model.MedicalHistory;
import com.techchallenge.history_service.repository.MedicalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de mensagens responsável por sincronizar o histórico médico.
 * <p>
 * Esta classe escuta a fila {@code history.queue} e processa eventos de agendamento
 * para manter um registro histórico atualizado. Ela garante que o serviço de
 * histórico tenha uma base de dados própria e independente para consultas via GraphQL.
 * </p>
 * <p>
 * <b>Padrão Arquitetural:</b> Implementa a consistência eventual entre o serviço
 * de agendamento e o serviço de histórico.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Component
@RequiredArgsConstructor
public class MedicalHistoryConsumer {

    private final MedicalHistoryRepository repository;

    /**
     * Recebe e processa eventos de agendamento vindos do RabbitMQ.
     * <p>
     * O método realiza uma operação de <b>Upsert</b> (Update ou Insert):
     * <ul>
     * <li>Se o ID do agendamento já existir no histórico, os dados são atualizados.</li>
     * <li>Caso contrário, um novo registro de histórico é criado.</li>
     * </ul>
     * </p>
     *
     * @param dto Objeto de transferência contendo os dados brutos do agendamento.
     */
    @RabbitListener(queues = RabbitConfig.HISTORY_QUEUE)
    public void receiveAppointmentEvent(AppointmentEventDTO dto) {

        MedicalHistory history = repository.findById(dto.getId())
                .orElse(new MedicalHistory());

        history.setId(dto.getId());
        history.setPatientName(dto.getPatientName());
        history.setPatientEmail(dto.getPatientEmail());
        history.setPatientUsername(dto.getPatientUsername());
        history.setDoctorName(dto.getDoctorName());
        history.setAppointmentDate(dto.getAppointmentDate());
        history.setStatus(dto.getStatus());
        history.setNotes("Integrado via JSON DTO");

        repository.save(history);
        System.out.println("🔄 Sincronização concluída para ID: " + dto.getId() + " com status: " + dto.getStatus());
    }
}