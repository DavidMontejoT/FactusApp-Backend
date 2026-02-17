-- Agregar campos para integración con Factus API (DIAN)
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS factus_invoice_id VARCHAR(100);
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS cufe VARCHAR(200);
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS qr_code VARCHAR(500);
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS dian_status VARCHAR(50);
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS factus_error_message TEXT;

-- Comentario sobre la migración
COMMENT ON COLUMN invoices.factus_invoice_id IS 'ID de factura en Factus API';
COMMENT ON COLUMN invoices.cufe IS 'Código único FE - CUFE DIAN';
COMMENT ON COLUMN invoices.qr_code IS 'Código QR de la factura electrónica';
COMMENT ON COLUMN invoices.dian_status IS 'Estado en DIAN (REGISTERED, REJECTED, ACCEPTED, CANCELLED)';
COMMENT ON COLUMN invoices.factus_error_message IS 'Mensaje de error si falló el envío a DIAN';
