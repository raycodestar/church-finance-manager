package com.example.utils

object SupabaseSchema {
    val SQL_DDL = """
        -- ========================================================
        -- CHURCH FINANCE MANAGER - SUPABASE POSTGRESQL SCHEMA & RLS
        -- Single Church & Single Administrator Architecture
        -- ========================================================

        -- Enable UUID extension
        CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

        -- 1. ADMIN PROFILES
        CREATE TABLE IF NOT EXISTS admin_profiles (
            id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
            email TEXT NOT NULL UNIQUE,
            full_name TEXT NOT NULL,
            created_at TIMESTAMPTZ DEFAULT NOW(),
            updated_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 2. CHURCH PROFILES
        CREATE TABLE IF NOT EXISTS church_profiles (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            name TEXT NOT NULL,
            logo_url TEXT,
            location TEXT NOT NULL,
            contact_phone TEXT NOT NULL,
            contact_email TEXT NOT NULL,
            default_currency TEXT DEFAULT 'UGX',
            financial_year_start_month INT DEFAULT 1,
            is_initialized BOOLEAN DEFAULT TRUE,
            created_at TIMESTAMPTZ DEFAULT NOW(),
            updated_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 3. GATHERING TYPES
        CREATE TABLE IF NOT EXISTS gathering_types (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            name TEXT NOT NULL,
            is_custom BOOLEAN DEFAULT FALSE,
            is_deleted BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 4. GATHERINGS
        CREATE TABLE IF NOT EXISTS gatherings (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            name TEXT NOT NULL,
            gathering_type_id UUID REFERENCES gathering_types(id) ON DELETE SET NULL,
            gathering_type_name TEXT NOT NULL,
            date_millis BIGINT NOT NULL,
            start_time TEXT,
            description TEXT,
            is_deleted BOOLEAN DEFAULT FALSE,
            deleted_at TIMESTAMPTZ,
            created_at TIMESTAMPTZ DEFAULT NOW(),
            updated_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 5. INCOME CATEGORIES
        CREATE TABLE IF NOT EXISTS income_categories (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            name TEXT NOT NULL,
            is_default BOOLEAN DEFAULT FALSE,
            is_disabled BOOLEAN DEFAULT FALSE,
            is_deleted BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 6. EXPENSE CATEGORIES
        CREATE TABLE IF NOT EXISTS expense_categories (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            name TEXT NOT NULL,
            is_default BOOLEAN DEFAULT FALSE,
            is_disabled BOOLEAN DEFAULT FALSE,
            is_deleted BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 7. INCOME TRANSACTIONS
        CREATE TABLE IF NOT EXISTS income_transactions (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            gathering_id UUID REFERENCES gatherings(id) ON DELETE SET NULL,
            gathering_name TEXT,
            category_id UUID REFERENCES income_categories(id) ON DELETE RESTRICT,
            category_name TEXT NOT NULL,
            amount BIGINT NOT NULL CHECK (amount > 0),
            payment_method TEXT NOT NULL,
            date_millis BIGINT NOT NULL,
            description TEXT,
            reference_number TEXT,
            attachment_url TEXT,
            is_deleted BOOLEAN DEFAULT FALSE,
            deleted_at TIMESTAMPTZ,
            created_at TIMESTAMPTZ DEFAULT NOW(),
            updated_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 8. EXPENSE TRANSACTIONS
        CREATE TABLE IF NOT EXISTS expense_transactions (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            title TEXT NOT NULL,
            category_id UUID REFERENCES expense_categories(id) ON DELETE RESTRICT,
            category_name TEXT NOT NULL,
            amount BIGINT NOT NULL CHECK (amount > 0),
            date_millis BIGINT NOT NULL,
            payment_method TEXT NOT NULL,
            payee TEXT,
            description TEXT,
            attachment_url TEXT,
            reference_number TEXT,
            gathering_id UUID REFERENCES gatherings(id) ON DELETE SET NULL,
            gathering_name TEXT,
            is_deleted BOOLEAN DEFAULT FALSE,
            deleted_at TIMESTAMPTZ,
            created_at TIMESTAMPTZ DEFAULT NOW(),
            updated_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- 9. ACTIVITY LOGS
        CREATE TABLE IF NOT EXISTS activity_logs (
            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
            admin_id UUID NOT NULL REFERENCES admin_profiles(id) ON DELETE CASCADE,
            action_type TEXT NOT NULL,
            record_type TEXT NOT NULL,
            record_id TEXT NOT NULL,
            description TEXT NOT NULL,
            timestamp_millis BIGINT NOT NULL,
            created_at TIMESTAMPTZ DEFAULT NOW()
        );

        -- ========================================================
        -- ROW-LEVEL SECURITY (RLS) POLICIES
        -- ========================================================

        ALTER TABLE admin_profiles ENABLE ROW LEVEL SECURITY;
        ALTER TABLE church_profiles ENABLE ROW LEVEL SECURITY;
        ALTER TABLE gathering_types ENABLE ROW LEVEL SECURITY;
        ALTER TABLE gatherings ENABLE ROW LEVEL SECURITY;
        ALTER TABLE income_categories ENABLE ROW LEVEL SECURITY;
        ALTER TABLE expense_categories ENABLE ROW LEVEL SECURITY;
        ALTER TABLE income_transactions ENABLE ROW LEVEL SECURITY;
        ALTER TABLE expense_transactions ENABLE ROW LEVEL SECURITY;
        ALTER TABLE activity_logs ENABLE ROW LEVEL SECURITY;

        -- Admin Profiles RLS
        CREATE POLICY "Admin view self profile" ON admin_profiles FOR SELECT USING (auth.uid() = id);
        CREATE POLICY "Admin insert self profile" ON admin_profiles FOR INSERT WITH CHECK (auth.uid() = id);
        CREATE POLICY "Admin update self profile" ON admin_profiles FOR UPDATE USING (auth.uid() = id);

        -- Church Profiles RLS
        CREATE POLICY "Admin church profile access" ON church_profiles FOR ALL USING (auth.uid() = admin_id);

        -- Gathering Types RLS
        CREATE POLICY "Admin gathering types access" ON gathering_types FOR ALL USING (auth.uid() = admin_id);

        -- Gatherings RLS
        CREATE POLICY "Admin gatherings access" ON gatherings FOR ALL USING (auth.uid() = admin_id);

        -- Categories RLS
        CREATE POLICY "Admin income categories access" ON income_categories FOR ALL USING (auth.uid() = admin_id);
        CREATE POLICY "Admin expense categories access" ON expense_categories FOR ALL USING (auth.uid() = admin_id);

        -- Transactions RLS
        CREATE POLICY "Admin income transactions access" ON income_transactions FOR ALL USING (auth.uid() = admin_id);
        CREATE POLICY "Admin expense transactions access" ON expense_transactions FOR ALL USING (auth.uid() = admin_id);

        -- Activity Logs RLS
        CREATE POLICY "Admin activity logs access" ON activity_logs FOR ALL USING (auth.uid() = admin_id);

        -- ========================================================
        -- SUPABASE STORAGE BUCKET POLICIES
        -- ========================================================

        INSERT INTO storage.buckets (id, name, public)
        VALUES ('transaction-attachments', 'transaction-attachments', false)
        ON CONFLICT (id) DO NOTHING;

        CREATE POLICY "Authenticated user upload attachment" ON storage.objects
        FOR INSERT WITH CHECK (bucket_id = 'transaction-attachments' AND auth.role() = 'authenticated');

        CREATE POLICY "Authenticated user select attachment" ON storage.objects
        FOR SELECT USING (bucket_id = 'transaction-attachments' AND auth.role() = 'authenticated');
    """.trimIndent()
}
