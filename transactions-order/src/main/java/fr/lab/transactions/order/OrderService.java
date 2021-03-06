package fr.lab.transactions.order;

import fr.lab.transactions.order.command.OrderCreation;
import fr.lab.transactions.tx.DistributedTransaction;
import fr.lab.transactions.tx.RollbackListener;
import fr.lab.transactions.tx.audit.AuditReaderService;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@RequiredArgsConstructor
@DistributedTransaction
@Transactional(rollbackOn = Exception.class)
public class OrderService {

    private final OrderMapper orderMapper;

    private final AuditReaderService auditReaderService;

    public Order createOrder(final OrderCreation orderCreation) {
        OrderEntity order = orderMapper.toOrderEntity(orderCreation);
        order.persist();
        return orderMapper.toOrder(order);
    }

    @RollbackListener
    public void onRollback(long transactionId) {
        auditReaderService.findRollbackEntity(OrderEntity.class, transactionId);
    }
}
