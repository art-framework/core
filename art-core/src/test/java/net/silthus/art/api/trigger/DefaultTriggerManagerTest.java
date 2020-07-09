package net.silthus.art.api.trigger;

import net.silthus.art.api.storage.StorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("TriggerManager")
class DefaultTriggerManagerTest {

    private TriggerManager manager;
    private TriggerFactory<?> test1Factory;
    private TriggerFactory<?> test2Factory;

    @BeforeEach
    void beforeEach() {
        TriggerFactoryProvider provider = mock(TriggerFactoryProvider.class);
        when(provider.create(any())).thenAnswer(invocation -> new TriggerFactory<>(invocation.getArgument(0), mock(StorageProvider.class)));
        manager = spy(new DefaultTriggerManager(provider));
        test1Factory = mock(TriggerFactory.class);
        when(manager.getFactory(eq("test1"))).thenReturn(Optional.of(test1Factory));
        test2Factory = mock(TriggerFactory.class);
        when(manager.getFactory(eq("test2"))).thenReturn(Optional.of(test2Factory));
    }

    @Nested
    @DisplayName("addListener(String, TriggerListener)")
    class addListener {

        @Test
        @DisplayName("should add listener to matching factory")
        void shouldAddListenerToCorrespondingFactory() {

            manager.addListener("test1", String.class, target -> {
            });
            verify(test1Factory, times(1)).addListener(eq(String.class), any());
            verify(test2Factory, times(0)).addListener(eq(String.class), any());
        }
    }

    @Nested
    @DisplayName("create(...)")
    class create {

        @Test
        @DisplayName("should add trigger class if no method is annotated")
        void shouldAddTriggerClassIfNoMethodIsAnnotated() {
            assertThat(manager.create(new TriggerFactoryTest.MyTrigger()))
                    .hasSize(1)
                    .first()
                    .extracting(TriggerFactory::getMethod)
                    .isNull();
        }

        @Test
        @DisplayName("should register all annotated trigger methods as separate factories")
        void shouldRegisterAllAnnotatedMethodsAsSeparateTrigger() {
            assertThat(manager.create(new TriggerFactoryTest.MyMultiTrigger()))
                    .hasSize(3);
        }
    }
}